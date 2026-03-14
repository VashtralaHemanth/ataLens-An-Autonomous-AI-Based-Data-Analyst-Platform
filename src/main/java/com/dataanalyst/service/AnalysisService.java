package com.dataanalyst.service;

import com.dataanalyst.model.AnalysisResult;
import com.dataanalyst.model.Dataset;
import com.dataanalyst.model.User;
import com.dataanalyst.repository.AnalysisRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final AnalysisRepository analysisRepository;
    private final DatasetService datasetService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.ai.engine.url}")
    private String aiEngineUrl;

    public AnalysisService(AnalysisRepository analysisRepository,
                           DatasetService datasetService,
                           RestTemplate restTemplate,
                           ObjectMapper objectMapper) {
        this.analysisRepository = analysisRepository;
        this.datasetService = datasetService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public AnalysisResult getAnalysisByDatasetId(Long datasetId, User user) {
        Dataset dataset = datasetService.getDatasetById(datasetId, user);
        return analysisRepository.findByDataset(dataset)
                .orElseThrow(() -> new RuntimeException("No analysis found for this dataset."));
    }

    public Optional<AnalysisResult> findAnalysisByDatasetId(Long datasetId) {
        return analysisRepository.findByDatasetId(datasetId);
    }

    @Async
    public void runAnalysisAsync(Long datasetId, User user) {
        Dataset dataset = datasetService.getDatasetById(datasetId, user);
        datasetService.updateDatasetStatus(datasetId, Dataset.DatasetStatus.ANALYZING);

        AnalysisResult analysis = analysisRepository.findByDataset(dataset)
                .orElse(AnalysisResult.builder().dataset(dataset).build());
        analysis.setStatus(AnalysisResult.AnalysisStatus.RUNNING);
        analysis = analysisRepository.save(analysis);

        long startTime = Instant.now().toEpochMilli();

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("dataset_id", datasetId);
            requestBody.put("file_path", dataset.getFilePath());
            requestBody.put("filename", dataset.getOriginalFilename());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    aiEngineUrl + "/analyze", entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode result = objectMapper.readTree(response.getBody());

                String summaryJson  = objectMapper.writeValueAsString(result.get("summary"));
                String chartsJson   = objectMapper.writeValueAsString(result.get("charts"));
                String insightsText = result.has("insights") ? result.get("insights").asText() : "";

                JsonNode summary = result.get("summary");
                if (summary != null && summary.has("shape")) {
                    long rows = summary.get("shape").get(0).asLong();
                    int  cols = summary.get("shape").get(1).asInt();
                    datasetService.updateDatasetStats(datasetId, rows, cols);
                }

                analysis.setSummaryJson(summaryJson);
                analysis.setChartsJson(chartsJson);
                analysis.setInsightsText(insightsText);
                analysis.setStatus(AnalysisResult.AnalysisStatus.COMPLETED);
                analysis.setAnalysisDurationMs(Instant.now().toEpochMilli() - startTime);
                analysisRepository.save(analysis);

                datasetService.updateDatasetStatus(datasetId, Dataset.DatasetStatus.COMPLETED);
                log.info("Analysis completed for dataset {} in {}ms", datasetId,
                        Instant.now().toEpochMilli() - startTime);
            } else {
                throw new RuntimeException("AI engine returned an error: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Analysis failed for dataset {}: {}", datasetId, e.getMessage());
            analysis.setStatus(AnalysisResult.AnalysisStatus.FAILED);
            analysis.setErrorMessage(e.getMessage());
            analysisRepository.save(analysis);
            datasetService.updateDatasetStatus(datasetId, Dataset.DatasetStatus.FAILED);
        }
    }
}
