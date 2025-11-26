package com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.repository;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.LastMileDriver;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverId;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * LastMileDriver Domain Repository Interface
 */
public interface LastMileDriverRepository {

    LastMileDriver save(LastMileDriver driver);

    Optional<LastMileDriver> findById(LastMileDriverId id);

    Optional<LastMileDriver> findByUserId(String userId);

    /**
     * 허브별 배정 가능한 드라이버 조회
     * 우선순위 낮은 순서 (배정 횟수 적은 순서)
     */
    List<LastMileDriver> findAvailableDriversByHubId(String hubId);

    Page<LastMileDriver> findByHubId(String hubId, Pageable pageable);

    Page<LastMileDriver> findByHubIdAndStatus(String hubId, LastMileDriverStatus status, Pageable pageable);

    Page<LastMileDriver> findAll(Pageable pageable);

    boolean existsByUserId(String userId);
}
