package com.dataanalyst.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "datasets")
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String filename;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Column(name = "row_count")
    private Long rowCount;

    @Column(name = "column_count")
    private Integer columnCount;

    @Enumerated(EnumType.STRING)
    private DatasetStatus status = DatasetStatus.PENDING;

    @Column(name = "content_type")
    private String contentType;

    public Dataset() {}

    public Dataset(Long id, User user, String filename, String originalFilename,
                   String filePath, Long fileSize, LocalDateTime uploadDate,
                   Long rowCount, Integer columnCount, DatasetStatus status, String contentType) {
        this.id = id; this.user = user; this.filename = filename;
        this.originalFilename = originalFilename; this.filePath = filePath;
        this.fileSize = fileSize; this.uploadDate = uploadDate;
        this.rowCount = rowCount; this.columnCount = columnCount;
        this.status = status; this.contentType = contentType;
    }

    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
        if (status == null) status = DatasetStatus.PENDING;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private User user; private String filename;
        private String originalFilename; private String filePath;
        private Long fileSize; private LocalDateTime uploadDate;
        private Long rowCount; private Integer columnCount;
        private DatasetStatus status = DatasetStatus.PENDING; private String contentType;

        public Builder id(Long id)                          { this.id = id; return this; }
        public Builder user(User user)                      { this.user = user; return this; }
        public Builder filename(String filename)            { this.filename = filename; return this; }
        public Builder originalFilename(String of)         { this.originalFilename = of; return this; }
        public Builder filePath(String filePath)            { this.filePath = filePath; return this; }
        public Builder fileSize(Long fileSize)              { this.fileSize = fileSize; return this; }
        public Builder uploadDate(LocalDateTime ud)         { this.uploadDate = ud; return this; }
        public Builder rowCount(Long rowCount)              { this.rowCount = rowCount; return this; }
        public Builder columnCount(Integer columnCount)     { this.columnCount = columnCount; return this; }
        public Builder status(DatasetStatus status)         { this.status = status; return this; }
        public Builder contentType(String contentType)      { this.contentType = contentType; return this; }

        public Dataset build() {
            return new Dataset(id, user, filename, originalFilename, filePath,
                    fileSize, uploadDate, rowCount, columnCount, status, contentType);
        }
    }

    public Long getId()                            { return id; }
    public void setId(Long id)                     { this.id = id; }
    public User getUser()                          { return user; }
    public void setUser(User user)                 { this.user = user; }
    public String getFilename()                    { return filename; }
    public void setFilename(String filename)       { this.filename = filename; }
    public String getOriginalFilename()            { return originalFilename; }
    public void setOriginalFilename(String of)     { this.originalFilename = of; }
    public String getFilePath()                    { return filePath; }
    public void setFilePath(String filePath)       { this.filePath = filePath; }
    public Long getFileSize()                      { return fileSize; }
    public void setFileSize(Long fileSize)         { this.fileSize = fileSize; }
    public LocalDateTime getUploadDate()           { return uploadDate; }
    public void setUploadDate(LocalDateTime ud)    { this.uploadDate = ud; }
    public Long getRowCount()                      { return rowCount; }
    public void setRowCount(Long rowCount)         { this.rowCount = rowCount; }
    public Integer getColumnCount()                { return columnCount; }
    public void setColumnCount(Integer columnCount){ this.columnCount = columnCount; }
    public DatasetStatus getStatus()               { return status; }
    public void setStatus(DatasetStatus status)    { this.status = status; }
    public String getContentType()                 { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dataset)) return false;
        Dataset d = (Dataset) o;
        return Objects.equals(id, d.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() {
        return "Dataset{id=" + id + ", filename='" + filename + "', status=" + status + '}';
    }

    public enum DatasetStatus { PENDING, ANALYZING, COMPLETED, FAILED }
}
