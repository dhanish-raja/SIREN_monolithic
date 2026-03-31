package com.siren.service;

import com.siren.dto.*;
import com.siren.model.Request;
import com.siren.model.Office;
import com.siren.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class RequestService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private VideoService videoService;

    @Autowired
    private RoutingService routingService;

    private static final String UPLOAD_DIR =
            System.getProperty("user.home") + "/siren-uploads/";

    public Object handleRequest(MultipartFile file, String description,
                                Double latitude, Double longitude) {

        System.out.println("Handle Request");

        String filePath = null;
        String type = "TEXT";
        byte[] fileBytes = null;

        try {
            if (file != null && !file.isEmpty()) {

                type = file.getContentType();
                fileBytes = file.getBytes();

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path uploadPath = Paths.get(UPLOAD_DIR);

                Files.createDirectories(uploadPath);

                Path destPath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), destPath, StandardCopyOption.REPLACE_EXISTING);

                filePath = destPath.toAbsolutePath().toString();
            }

        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }

        // ---------------- AI CALL ----------------
        GeminiResponseDTO aiResponse;
        String safeDescription = description != null ? description : "";

        try {
            if (fileBytes != null && type != null) {

                if (type.startsWith("image/")) {

                    aiResponse = geminiService.analyzeImage(
                            fileBytes,
                            safeDescription,
                            type
                    );

                } else if (type.startsWith("video/")) {

                    List<byte[]> frames = videoService.extractFrames(filePath);

                    aiResponse = geminiService.analyzeMultipleImages(
                            frames,
                            safeDescription
                    );

                } else {

                    aiResponse = geminiService.analyzeText(safeDescription);
                }

            } else {

                aiResponse = geminiService.analyzeText(safeDescription);
            }

        } catch (Exception e) {
            throw new RuntimeException("AI processing failed: " + e.getMessage());
        }

        // ---------------- ROUTING ----------------
        Office assignedOffice = null;

        if (latitude != null && longitude != null) {
            try {
                assignedOffice = routingService.findNearestOffice(
                        aiResponse.getDepartment(),
                        latitude,
                        longitude
                );
            } catch (Exception e) {
                // Optional: log error, but don’t break request flow
                System.out.println("Routing failed: " + e.getMessage());
            }
        }

        // ---------------- ENTITY ----------------
        Request request = new Request();
        request.setType(type);
        request.setMediaUrl(filePath);
        request.setDescription(description);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setDepartment(aiResponse.getDepartment());
        request.setSummary(aiResponse.getSummary());
        request.setStatus("OPEN");
        request.setCreatedAt(LocalDateTime.now());

        // 🔥 IMPORTANT
        request.setAssignedOffice(assignedOffice);

        requestRepository.save(request);

        return mapToDTO(request);
    }

    private RequestResponseDTO mapToDTO(Request request) {

        RequestResponseDTO dto = new RequestResponseDTO();

        dto.setId(request.getId());
        dto.setType(request.getType());
        dto.setDescription(request.getDescription());
        dto.setDepartment(request.getDepartment());
        dto.setSummary(request.getSummary());
        dto.setStatus(request.getStatus());
        dto.setLatitude(request.getLatitude());
        dto.setLongitude(request.getLongitude());

        if (request.getAssignedOffice() != null) {
            OfficeDTO officeDTO = new OfficeDTO();
            officeDTO.setId(request.getAssignedOffice().getId());
            officeDTO.setName(request.getAssignedOffice().getName());
            officeDTO.setDepartmentId(request.getAssignedOffice().getDepartmentId());

            dto.setAssignedOffice(officeDTO);
        }

        return dto;
    }

    public RequestResponseDTO updateStatus(Long requestId, String status) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(status);

        requestRepository.save(request);

        return mapToDTO(request);
    }

    public List<RequestResponseDTO> getRequests(String department, String status) {

        List<Request> requests;

        if (department != null && status != null) {
            requests = requestRepository.findByDepartmentAndStatus(department, status);
        } else if (department != null) {
            requests = requestRepository.findByDepartment(department);
        } else if (status != null) {
            requests = requestRepository.findByStatus(status);
        } else {
            requests = requestRepository.findAll();
        }

        return requests.stream()
                .map(this::mapToDTO)
                .toList();
    }

}