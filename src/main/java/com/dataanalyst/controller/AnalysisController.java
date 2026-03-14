package com.dataanalyst.controller;

import com.dataanalyst.model.AnalysisResult;
import com.dataanalyst.model.User;
import com.dataanalyst.service.AnalysisService;
import com.dataanalyst.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final AuthService authService;

    public AnalysisController(AnalysisService analysisService, AuthService authService) {
        this.analysisService = analysisService;
        this.authService = authService;
    }

    @PostMapping("/run/{datasetId}")
    public ResponseEntity<?> runAnalysis(@PathVariable Long datasetId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = authService.getCurrentUser(userDetails.getUsername());
            analysisService.runAnalysisAsync(datasetId, user);
            return ResponseEntity.accepted()
                    .body(Map.of("message", "Analysis started.", "datasetId", datasetId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to start analysis: " + e.getMessage()));
        }
    }

    @GetMapping("/{datasetId}")
    public ResponseEntity<?> getAnalysis(@PathVariable Long datasetId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            authService.getCurrentUser(userDetails.getUsername()); // verify access
            Optional<AnalysisResult> analysisOpt = analysisService.findAnalysisByDatasetId(datasetId);
            if (analysisOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(toDto(analysisOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to fetch analysis: " + e.getMessage()));
        }
    }

    private Map<String, Object> toDto(AnalysisResult a) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", a.getId());
        dto.put("datasetId", a.getDataset().getId());
        dto.put("summaryJson", a.getSummaryJson());
        dto.put("chartsJson", a.getChartsJson());
        dto.put("insightsText", a.getInsightsText());
        dto.put("status", a.getStatus());
        dto.put("analysisDurationMs", a.getAnalysisDurationMs());
        dto.put("createdAt", a.getCreatedAt());
        dto.put("errorMessage", a.getErrorMessage());
        return dto;
    }
}
