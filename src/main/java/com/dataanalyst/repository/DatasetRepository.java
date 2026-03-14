package com.dataanalyst.repository;

import com.dataanalyst.model.Dataset;
import com.dataanalyst.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    List<Dataset> findByUserOrderByUploadDateDesc(User user);
    Optional<Dataset> findByIdAndUser(Long id, User user);
    long countByUser(User user);
}
