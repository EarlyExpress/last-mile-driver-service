package com.early_express.last_mile_driver_service.domain.last_mile_driver.application.command.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * LastMileDriver Command DTO
 */
public class LastMileDriverCommandDto {

    /**
     * 드라이버 생성 Command
     */
    @Getter
    @Builder
    public static class CreateCommand {
        private String userId;
        private String hubId;
        private String name;
        private String createdBy;
    }

    /**
     * 배송 배정 Command (허브별)
     */
    @Getter
    @Builder
    public static class AssignDeliveryCommand {
        private String hubId;
        private String deliveryId;
    }

    /**
     * 배송 완료 Command
     */
    @Getter
    @Builder
    public static class CompleteDeliveryCommand {
        private String driverId;
        private Long deliveryTimeMin;
    }

    /**
     * 배송 취소 Command
     */
    @Getter
    @Builder
    public static class CancelDeliveryCommand {
        private String driverId;
    }

    /**
     * 근무 시작 Command
     */
    @Getter
    @Builder
    public static class StartWorkCommand {
        private String driverId;
    }

    /**
     * 근무 종료 Command
     */
    @Getter
    @Builder
    public static class EndWorkCommand {
        private String driverId;
    }

    /**
     * 휴직 처리 Command
     */
    @Getter
    @Builder
    public static class DeactivateCommand {
        private String driverId;
        private String deactivatedBy;
    }

    /**
     * 복직 처리 Command
     */
    @Getter
    @Builder
    public static class ActivateCommand {
        private String driverId;
    }

    /**
     * 드라이버 배정 결과
     */
    @Getter
    @Builder
    public static class DriverAssignResult {
        private String driverId;
        private String userId;
        private String hubId;
        private String driverName;
        private String status;
        private LocalDateTime assignedAt;

        public static DriverAssignResult success(String driverId, String userId, String hubId,
                                                 String driverName, String status) {
            return DriverAssignResult.builder()
                    .driverId(driverId)
                    .userId(userId)
                    .hubId(hubId)
                    .driverName(driverName)
                    .status(status)
                    .assignedAt(LocalDateTime.now())
                    .build();
        }
    }

    /**
     * 드라이버 생성 결과
     */
    @Getter
    @Builder
    public static class CreateResult {
        private String driverId;
        private String userId;
        private String hubId;
        private String name;
        private String status;

        public static CreateResult success(String driverId, String userId, String hubId,
                                           String name, String status) {
            return CreateResult.builder()
                    .driverId(driverId)
                    .userId(userId)
                    .hubId(hubId)
                    .name(name)
                    .status(status)
                    .build();
        }
    }
}