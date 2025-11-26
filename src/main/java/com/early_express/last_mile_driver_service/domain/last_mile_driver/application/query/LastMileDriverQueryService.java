package com.early_express.last_mile_driver_service.domain.last_mile_driver.application.query;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.query.dto.LastMileDriverQueryDto.*;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.exception.LastMileDriverErrorCode;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.exception.LastMileDriverException;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.LastMileDriver;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverId;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverStatus;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.repository.LastMileDriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * LastMileDriver Query Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LastMileDriverQueryService {

    private final LastMileDriverRepository lastMileDriverRepository;

    /**
     * ID로 상세 조회
     */
    public LastMileDriverDetailResponse findById(String driverId) {
        LastMileDriver driver = lastMileDriverRepository.findById(LastMileDriverId.of(driverId))
                .orElseThrow(() -> new LastMileDriverException(
                        LastMileDriverErrorCode.LAST_MILE_DRIVER_NOT_FOUND,
                        "최종 배송 담당자를 찾을 수 없습니다: " + driverId
                ));

        return LastMileDriverDetailResponse.from(driver);
    }

    /**
     * 사용자 ID로 조회
     */
    public LastMileDriverDetailResponse findByUserId(String userId) {
        LastMileDriver driver = lastMileDriverRepository.findByUserId(userId)
                .orElseThrow(() -> new LastMileDriverException(
                        LastMileDriverErrorCode.LAST_MILE_DRIVER_NOT_FOUND,
                        "해당 사용자의 배송 담당자 정보를 찾을 수 없습니다: " + userId
                ));

        return LastMileDriverDetailResponse.from(driver);
    }

    /**
     * 허브별 드라이버 목록 조회
     */
    public Page<LastMileDriverResponse> findByHubId(String hubId, Pageable pageable) {
        return lastMileDriverRepository.findByHubId(hubId, pageable)
                .map(LastMileDriverResponse::from);
    }

    /**
     * 허브별 상태별 드라이버 목록 조회
     */
    public Page<LastMileDriverResponse> findByHubIdAndStatus(String hubId, LastMileDriverStatus status, Pageable pageable) {
        return lastMileDriverRepository.findByHubIdAndStatus(hubId, status, pageable)
                .map(LastMileDriverResponse::from);
    }

    /**
     * 전체 목록 조회
     */
    public Page<LastMileDriverResponse> findAll(Pageable pageable) {
        return lastMileDriverRepository.findAll(pageable)
                .map(LastMileDriverResponse::from);
    }
}
