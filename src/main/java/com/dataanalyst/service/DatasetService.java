package com.dataanalyst.service;

import com.dataanalyst.model.Dataset;
import com.dataanalyst.model.User;
import com.dataanalyst.repository.DatasetRepository;
import com.dataanalyst.repository.AnalysisResultRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class DatasetService {

    private final DatasetRepository datasetRepository;
    private final AnalysisResultRepository analysisResultRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public DatasetService(DatasetRepository datasetRepository,
                          AnalysisResultRepository analysisResultRepository) {
        this.datasetRepository = datasetRepository;
        this.analysisResultRepository = analysisResultRepository;
    }

    public Dataset uploadDataset(MultipartFile file, User user) throws IOException {

        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();

        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : ".csv";

        String storedFilename = UUID.randomUUID() + extension;

        Path filePath = uploadPath.resolve(storedFilename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Dataset dataset = Dataset.builder()
                .user(user)
                .filename(storedFilename)
                .originalFilename(originalFilename)
                .filePath(filePath.toAbsolutePath().toString())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .status(Dataset.DatasetStatus.PENDING)
                .build();

        return datasetRepository.save(dataset);
    }

    public List<Dataset> getUserDatasets(User user) {
        return datasetRepository.findByUserOrderByUploadDateDesc(user);
    }

    public Dataset getDatasetById(Long id, User user) {

        return datasetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Dataset not found or access denied."));
    }

    @Transactional
    public void deleteDataset(Long id, User user) throws IOException {

        Dataset dataset = getDatasetById(id, user);

        // delete related analysis results first
        analysisResultRepository.deleteByDatasetId(id);

        // delete dataset file from storage
        Path filePath = Paths.get(dataset.getFilePath());
        Files.deleteIfExists(filePath);

        // delete dataset from database
        datasetRepository.delete(dataset);
    }

    public Dataset updateDatasetStatus(Long id, Dataset.DatasetStatus status) {

        Dataset dataset = datasetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dataset not found."));

        dataset.setStatus(status);

        return datasetRepository.save(dataset);
    }

    public Dataset updateDatasetStats(Long id, Long rowCount, Integer columnCount) {

        Dataset dataset = datasetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dataset not found."));

        dataset.setRowCount(rowCount);
        dataset.setColumnCount(columnCount);

        return datasetRepository.save(dataset);
    }
}