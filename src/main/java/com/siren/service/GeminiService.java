package com.siren.service;
import java.util.*;
import com.google.genai.Client;
import com.google.genai.types.*;
import com.siren.dto.GeminiResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.genai.types.Blob;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.google.genai.types.GenerateContentResponse;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private Client client;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    // ---------------- TEXT ----------------
    public GeminiResponseDTO analyzeText(String inputText) {
        System.out.println("pinging gemini (text)");
        try {
            String prompt = """
                Classify the following emergency into one of:
                POLICE, FIRE, MEDICAL.
                Give a short summary.
                Respond ONLY in raw JSON (no markdown, no code block):
                {
                  "department": "...",
                  "summary": "..."
                }
                Text: %s
                """.formatted(inputText);

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null
            );

            String text = clean(response.text());

            return mapper.readValue(text, GeminiResponseDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Gemini TEXT failed: " + e.getMessage());
        }
    }

    // ---------------- IMAGE ----------------
    public GeminiResponseDTO analyzeImage(byte[] imageBytes, String description, String mimeType) {
        System.out.println("pinging gemini (image)");
        try {
            String prompt = """
            Classify the situation into one of:
            POLICE, FIRE, MEDICAL.
            Give a short summary.
            Respond ONLY in raw JSON (no markdown, no code block):
            {
              "department": "...",
              "summary": "..."
            }
            Description: %s
            """.formatted(description != null ? description : "");



            Part imagePart = Part.builder()
                    .inlineData(Blob.builder()
                            .mimeType(mimeType != null ? mimeType : "application/octet-stream")
                            .data(imageBytes)  // pass raw bytes directly, no Base64 encoding
                            .build())
                    .build();

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    Content.fromParts(
                            Part.fromText(prompt),
                            imagePart
                    ),
                    null
            );

            String text = clean(response.text());
            return mapper.readValue(text, GeminiResponseDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Gemini IMAGE failed: " + e.getMessage());
        }
    }

    public GeminiResponseDTO analyzeMultipleImages(List<byte[]> images, String description) {
        System.out.println("pinging gemini (video or multiple images)");
        try {
            String prompt = """
        Classify the situation into one of:
        POLICE, FIRE, MEDICAL.
        Give a short summary.
        Respond ONLY in raw JSON (no markdown, no code block):
        {
          "department": "...",
          "summary": "..."
        }
        Description: %s
        """.formatted(description != null ? description : "");

            List<Part> parts = new ArrayList<>();

            // Add prompt first (same as analyzeImage)
            parts.add(Part.fromText(prompt));

            // Add all frames as image parts (same Blob usage)
            for (byte[] img : images) {
                Part imagePart = Part.builder()
                        .inlineData(
                                Blob.builder()
                                        .mimeType("image/jpeg") // frames are JPG from VideoService
                                        .data(img)
                                        .build()
                        )
                        .build();

                parts.add(imagePart);
            }

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    Content.fromParts(parts.toArray(new Part[0])),
                    null
            );

            String text = clean(response.text());
            return mapper.readValue(text, GeminiResponseDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Gemini VIDEO failed: " + e.getMessage());
        }
    }

    // ---------------- CLEANER ----------------
    private String clean(String text) {
        return text.replace("```json", "")
                .replace("```", "")
                .trim();
    }
}