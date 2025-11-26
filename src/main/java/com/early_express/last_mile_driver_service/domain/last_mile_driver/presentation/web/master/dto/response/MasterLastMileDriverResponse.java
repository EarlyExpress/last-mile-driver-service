package com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.web.master.dto.response;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.query.dto.LastMileDriverQueryDto.LastMileDriverResponse;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 마스터용 드라이버 응답
 */
@Getter
@Builder
public class MasterLastMileDriverResponse {

    private String driverId;
    private String userId;
    private String hubId;
    private String name;
    private LastMileDriverStatus status;
    private String statusDescription;
    private String currentDeliveryId;
    private Integer assignmentPriority;
    private Long totalDeliveries;
    private Long totalDeliveryTimeMin;
    private Long averageDeliveryTimeMin;
    private LocalDateTime lastDeliveryCompletedAt;
    private LocalDateTime availableFrom;
    private LocalDateTime createdAt;

    /**
     * Query DTO → Presentation DTO 변환
     */
    public static MasterLastMileDriverResponse from(LastMileDriverResponse driver) {
        return MasterLastMileDriverResponse.builder()
                .driverId(driver.getDriverId())
                .userId(driver.getUserId())
                .hubId(driver.getHubId())
                .name(driver.getName())
                .status(driver.getStatus())
                .statusDescription(driver.getStatus().getDescription())
                .currentDeliveryId(driver.getCurrentDeliveryId())
                .assignmentPriority(driver.getAssignmentPriority())
                .totalDeliveries(driver.getTotalDeliveries())
                .totalDeliveryTimeMin(driver.getTotalDeliveryTimeMin())
                .averageDeliveryTimeMin(driver.getAverageDeliveryTimeMin())
                .lastDeliveryCompletedAt(driver.getLastDeliveryCompletedAt())
                .availableFrom(driver.getAvailableFrom())
                .createdAt(driver.getCreatedAt())
                .build();
    }
}