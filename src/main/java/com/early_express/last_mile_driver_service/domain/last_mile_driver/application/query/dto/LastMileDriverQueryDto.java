package com.early_express.last_mile_driver_service.domain.last_mile_driver.application.query.dto;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.LastMileDriver;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * LastMileDriver Query DTO
 */
public class LastMileDriverQueryDto {

    /**
     * 드라이버 조회 응답
     */
    @Getter
    @Builder
    public static class LastMileDriverResponse {
        private String driverId;
        private String userId;
        private String hubId;
        private String name;
        private LastMileDriverStatus status;
        private String currentDeliveryId;
        private Integer assignmentPriority;
        private Long totalDeliveries;
        private Long totalDeliveryTimeMin;
        private Long averageDeliveryTimeMin;
        private LocalDateTime lastDeliveryCompletedAt;
        private LocalDateTime availableFrom;
        private LocalDateTime createdAt;

        public static LastMileDriverResponse from(LastMileDriver driver) {
            return LastMileDriverResponse.builder()
                    .driverId(driver.getIdValue())
                    .userId(driver.getUserId())
                    .hubId(driver.getHubId())
                    .name(driver.getName())
                    .status(driver.getStatus())
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

    /**
     * 드라이버 상세 응답
     */
    @Getter
    @Builder
    public static class LastMileDriverDetailResponse {
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
        private Boolean isAvailable;
        private Boolean isOnDelivery;
        private LocalDateTime createdAt;
        private String createdBy;

        public static LastMileDriverDetailResponse from(LastMileDriver driver) {
            return LastMileDriverDetailResponse.builder()
                    .driverId(driver.getIdValue())
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
                    .isAvailable(driver.isAvailable())
                    .isOnDelivery(driver.isOnDelivery())
                    .createdAt(driver.getCreatedAt())
                    .createdBy(driver.getCreatedBy())
                    .build();
        }
    }
}