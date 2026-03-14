package com.dataanalyst.repository;

import com.dataanalyst.model.AnalysisResult;
import com.dataanalyst.model.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<AnalysisResult, Long> {
    Optional<AnalysisResult> findByDataset(Dataset dataset);
    Optional<AnalysisResult> findByDatasetId(Long datasetId);
}
