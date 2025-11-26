package com.early_express.last_mile_driver_service.domain.last_mile_driver.infrastructure.persistence.jpa;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverStatus;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.infrastructure.persistence.entity.LastMileDriverEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * LastMileDriver JPA Repository
 */
public interface LastMileDriverJpaRepository extends JpaRepository<LastMileDriverEntity, String> {

    Optional<LastMileDriverEntity> findByIdAndIsDeletedFalse(String id);

    Optional<LastMileDriverEntity> findByUserIdAndIsDeletedFalse(String userId);

    boolean existsByUserIdAndIsDeletedFalse(String userId);

    Page<LastMileDriverEntity> findByHubIdAndIsDeletedFalse(String hubId, Pageable pageable);

    Page<LastMileDriverEntity> findByHubIdAndStatusAndIsDeletedFalse(
            String hubId,
            LastMileDriverStatus status,
            Pageable pageable
    );

    Page<LastMileDriverEntity> findByIsDeletedFalse(Pageable pageable);
}