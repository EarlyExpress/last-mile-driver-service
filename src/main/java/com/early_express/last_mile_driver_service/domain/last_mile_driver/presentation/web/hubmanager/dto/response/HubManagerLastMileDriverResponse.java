package com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.web.hubmanager.dto.response;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.query.dto.LastMileDriverQueryDto.LastMileDriverResponse;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 허브 관리자용 드라이버 응답
 */
@Getter
@Builder
public class HubManagerLastMileDriverResponse {

    private String driverId;
    private String userId;
    private String name;
    private LastMileDriverStatus status;
    private String statusDescription;
    private String currentDeliveryId;
    private Integer assignmentPriority;
    private Long totalDeliveries;
    private Long averageDeliveryTimeMin;
    private LocalDateTime availableFrom;

    /**
     * Query DTO → Presentation DTO 변환
     */
    public static HubManagerLastMileDriverResponse from(LastMileDriverResponse driver) {
        return HubManagerLastMileDriverResponse.builder()
                .driverId(driver.getDriverId())
                .userId(driver.getUserId())
                .name(driver.getName())
                .status(driver.getStatus())
                .statusDescription(driver.getStatus().getDescription())
                .currentDeliveryId(driver.getCurrentDeliveryId())
                .assignmentPriority(driver.getAssignmentPriority())
                .totalDeliveries(driver.getTotalDeliveries())
                .averageDeliveryTimeMin(driver.getAverageDeliveryTimeMin())
                .availableFrom(driver.getAvailableFrom())
                .build();
    }
}