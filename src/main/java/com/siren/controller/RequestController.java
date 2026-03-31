package com.siren.controller;

import com.siren.dto.RequestResponseDTO;
import com.siren.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class RequestController {

    @Autowired
    private RequestService requestService;

    // CREATE REQUEST
    @PostMapping("/request")
    public ResponseEntity<?> createRequest(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String description,
            @RequestParam Double latitude,
            @RequestParam Double longitude
    ) {
        return ResponseEntity.ok(
                requestService.handleRequest(file, description, latitude, longitude)
        );
    }

    // UPDATE STATUS
    @PatchMapping("/request/{id}/status")
    public ResponseEntity<RequestResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(
                requestService.updateStatus(id, status)
        );
    }

    @GetMapping("/requests")
    public ResponseEntity<List<RequestResponseDTO>> getRequests(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(
                requestService.getRequests(department, status)
        );
    }
}