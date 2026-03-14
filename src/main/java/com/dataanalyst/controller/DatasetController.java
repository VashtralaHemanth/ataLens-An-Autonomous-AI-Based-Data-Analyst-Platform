package com.dataanalyst.controller;

import com.dataanalyst.model.Dataset;
import com.dataanalyst.model.User;
import com.dataanalyst.service.AuthService;
import com.dataanalyst.service.DatasetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/datasets")
public class DatasetController {

    private final DatasetService datasetService;
    private final AuthService authService;

    public DatasetController(DatasetService datasetService, AuthService authService) {
        this.datasetService = datasetService;
        this.authService = authService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDataset(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {

            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }

            User user = authService.getCurrentUser(userDetails.getUsername());
            Dataset dataset = datasetService.uploadDataset(file, user);

            return ResponseEntity.ok(toDto(dataset));

        } catch (IOException e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "File upload failed: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllDatasets(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User user = authService.getCurrentUser(userDetails.getUsername());
        List<Dataset> datasets = datasetService.getUserDatasets(user);

        List<Map<String, Object>> result = datasets.stream()
                .map(this::toDto)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDataset(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {

            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }

            User user = authService.getCurrentUser(userDetails.getUsername());
            Dataset dataset = datasetService.getDatasetById(id, user);

            return ResponseEntity.ok(toDto(dataset));

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDataset(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {

            if (userDetails == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Unauthorized"));
            }

            User user = authService.getCurrentUser(userDetails.getUsername());

            datasetService.deleteDataset(id, user);

            return ResponseEntity.ok(Map.of("message", "Dataset deleted."));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Delete failed: " + e.getMessage()));
        }
    }

    private Map<String, Object> toDto(Dataset d) {

        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("id", d.getId());
        dto.put("filename", d.getOriginalFilename() != null ? d.getOriginalFilename() : d.getFilename());
        dto.put("fileSize", d.getFileSize());
        dto.put("uploadDate", d.getUploadDate());
        dto.put("rowCount", d.getRowCount());
        dto.put("columnCount", d.getColumnCount());
        dto.put("status", d.getStatus());
        dto.put("contentType", d.getContentType());

        return dto;
    }
}