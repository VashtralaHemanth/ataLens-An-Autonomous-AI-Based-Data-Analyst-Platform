package com.dataanalyst.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "analysis_results")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;

    @Column(name = "summary_json", columnDefinition = "LONGTEXT")
    private String summaryJson;

    @Column(name = "charts_json", columnDefinition = "LONGTEXT")
    private String chartsJson;

    @Column(name = "insights_text", columnDefinition = "LONGTEXT")
    private String insightsText;

    @Column(name = "analysis_duration_ms")
    private Long analysisDurationMs;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AnalysisStatus status = AnalysisStatus.PENDING;

    @Column(name = "error_message")
    private String errorMessage;

    public AnalysisResult() {}

    public AnalysisResult(Long id, Dataset dataset, String summaryJson, String chartsJson,
                          String insightsText, Long analysisDurationMs, LocalDateTime createdAt,
                          AnalysisStatus status, String errorMessage) {
        this.id = id; this.dataset = dataset; this.summaryJson = summaryJson;
        this.chartsJson = chartsJson; this.insightsText = insightsText;
        this.analysisDurationMs = analysisDurationMs; this.createdAt = createdAt;
        this.status = status; this.errorMessage = errorMessage;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = AnalysisStatus.PENDING;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private Dataset dataset; private String summaryJson;
        private String chartsJson; private String insightsText;
        private Long analysisDurationMs; private LocalDateTime createdAt;
        private AnalysisStatus status = AnalysisStatus.PENDING; private String errorMessage;

        public Builder id(Long id)                          { this.id = id; return this; }
        public Builder dataset(Dataset dataset)             { this.dataset = dataset; return this; }
        public Builder summaryJson(String summaryJson)      { this.summaryJson = summaryJson; return this; }
        public Builder chartsJson(String chartsJson)        { this.chartsJson = chartsJson; return this; }
        public Builder insightsText(String insightsText)    { this.insightsText = insightsText; return this; }
        public Builder analysisDurationMs(Long ms)          { this.analysisDurationMs = ms; return this; }
        public Builder createdAt(LocalDateTime ca)          { this.createdAt = ca; return this; }
        public Builder status(AnalysisStatus status)        { this.status = status; return this; }
        public Builder errorMessage(String errorMessage)    { this.errorMessage = errorMessage; return this; }

        public AnalysisResult build() {
            return new AnalysisResult(id, dataset, summaryJson, chartsJson, insightsText,
                    analysisDurationMs, createdAt, status, errorMessage);
        }
    }

    public Long getId()                                   { return id; }
    public void setId(Long id)                            { this.id = id; }
    public Dataset getDataset()                           { return dataset; }
    public void setDataset(Dataset dataset)               { this.dataset = dataset; }
    public String getSummaryJson()                        { return summaryJson; }
    public void setSummaryJson(String summaryJson)        { this.summaryJson = summaryJson; }
    public String getChartsJson()                         { return chartsJson; }
    public void setChartsJson(String chartsJson)          { this.chartsJson = chartsJson; }
    public String getInsightsText()                       { return insightsText; }
    public void setInsightsText(String insightsText)      { this.insightsText = insightsText; }
    public Long getAnalysisDurationMs()                   { return analysisDurationMs; }
    public void setAnalysisDurationMs(Long ms)            { this.analysisDurationMs = ms; }
    public LocalDateTime getCreatedAt()                   { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
    public AnalysisStatus getStatus()                     { return status; }
    public void setStatus(AnalysisStatus status)          { this.status = status; }
    public String getErrorMessage()                       { return errorMessage; }
    public void setErrorMessage(String errorMessage)      { this.errorMessage = errorMessage; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnalysisResult)) return false;
        AnalysisResult a = (AnalysisResult) o;
        return Objects.equals(id, a.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() {
        return "AnalysisResult{id=" + id + ", status=" + status + '}';
    }

    public enum AnalysisStatus { PENDING, RUNNING, COMPLETED, FAILED }
}
