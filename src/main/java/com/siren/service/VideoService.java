package com.siren.service;

import org.bytedeco.javacv.*;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

@Service
public class VideoService {

    public List<byte[]> extractFrames(String videoPath) {
        System.out.println("Extracting frames");
        List<byte[]> frames = new ArrayList<>();

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath)) {

            grabber.start();

            Java2DFrameConverter converter = new Java2DFrameConverter();

            int totalFrames = grabber.getLengthInFrames();
            int interval = Math.max(totalFrames / 5, 1);

            for (int i = 0; i < totalFrames; i += interval) {
                grabber.setFrameNumber(i);

                Frame frame = grabber.grabImage();
                if (frame == null) continue;

                BufferedImage img = converter.getBufferedImage(frame);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, "jpg", baos);

                frames.add(baos.toByteArray());
            }

            grabber.stop();

        } catch (Exception e) {
            throw new RuntimeException("Video processing failed: " + e.getMessage());
        }

        return frames;
    }
}