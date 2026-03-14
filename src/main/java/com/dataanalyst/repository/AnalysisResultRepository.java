package com.dataanalyst.repository;

import com.dataanalyst.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    void deleteByDatasetId(Long datasetId);

}