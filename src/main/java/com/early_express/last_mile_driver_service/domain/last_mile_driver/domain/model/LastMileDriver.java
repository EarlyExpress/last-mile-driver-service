package com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.exception.LastMileDriverErrorCode;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.exception.LastMileDriverException;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverId;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * LastMileDriver Aggregate Root
 * 최종 배송 담당자 (허브 소속)
 */
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LastMileDriver {

    private LastMileDriverId id;
    private String userId;
    private String hubId;  // 소속 허브 ID (HubDriver와 차이점)
    private String name;
    private LastMileDriverStatus status;
    private String currentDeliveryId;
    private Integer assignmentPriority;
    private Long totalDeliveries;
    private Long totalDeliveryTimeMin;
    private Long averageDeliveryTimeMin;
    private LocalDateTime lastDeliveryCompletedAt;
    private LocalDateTime availableFrom;

    // Audit 필드
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private LocalDateTime deletedAt;
    private String deletedBy;
    private boolean isDeleted;

    @Builder
    private LastMileDriver(LastMileDriverId id, String userId, String hubId, String name,
                           LastMileDriverStatus status, String currentDeliveryId,
                           Integer assignmentPriority, Long totalDeliveries,
                           Long totalDeliveryTimeMin, Long averageDeliveryTimeMin,
                           LocalDateTime lastDeliveryCompletedAt, LocalDateTime availableFrom,
                           LocalDateTime createdAt, String createdBy,
                           LocalDateTime updatedAt, String updatedBy,
                           LocalDateTime deletedAt, String deletedBy, boolean isDeleted) {
        this.id = id;
        this.userId = userId;
        this.hubId = hubId;
        this.name = name;
        this.status = status;
        this.currentDeliveryId = currentDeliveryId;
        this.assignmentPriority = assignmentPriority;
        this.totalDeliveries = totalDeliveries;
        this.totalDeliveryTimeMin = totalDeliveryTimeMin;
        this.averageDeliveryTimeMin = averageDeliveryTimeMin;
        this.lastDeliveryCompletedAt = lastDeliveryCompletedAt;
        this.availableFrom = availableFrom;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
        this.isDeleted = isDeleted;
    }

    // ===== 팩토리 메서드 =====

    /**
     * 새로운 LastMileDriver 생성
     */
    public static LastMileDriver create(String userId, String hubId, String name, String createdBy) {
        validateNotBlank(userId, "사용자 ID");
        validateNotBlank(hubId, "허브 ID");
        validateNotBlank(name, "이름");

        return LastMileDriver.builder()
                .id(null)  // Entity에서 UUID 생성
                .userId(userId)
                .hubId(hubId)
                .name(name)
                .status(LastMileDriverStatus.AVAILABLE)
                .assignmentPriority(0)
                .totalDeliveries(0L)
                .totalDeliveryTimeMin(0L)
                .averageDeliveryTimeMin(0L)
                .availableFrom(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .isDeleted(false)
                .build();
    }

    /**
     * DB 조회 후 도메인 복원용
     */
    public static LastMileDriver reconstitute(
            LastMileDriverId id, String userId, String hubId, String name,
            LastMileDriverStatus status, String currentDeliveryId,
            Integer assignmentPriority, Long totalDeliveries,
            Long totalDeliveryTimeMin, Long averageDeliveryTimeMin,
            LocalDateTime lastDeliveryCompletedAt, LocalDateTime availableFrom,
            LocalDateTime createdAt, String createdBy,
            LocalDateTime updatedAt, String updatedBy,
            LocalDateTime deletedAt, String deletedBy, boolean isDeleted) {

        return LastMileDriver.builder()
                .id(id)
                .userId(userId)
                .hubId(hubId)
                .name(name)
                .status(status)
                .currentDeliveryId(currentDeliveryId)
                .assignmentPriority(assignmentPriority)
                .totalDeliveries(totalDeliveries)
                .totalDeliveryTimeMin(totalDeliveryTimeMin)
                .averageDeliveryTimeMin(averageDeliveryTimeMin)
                .lastDeliveryCompletedAt(lastDeliveryCompletedAt)
                .availableFrom(availableFrom)
                .createdAt(createdAt)
                .createdBy(createdBy)
                .updatedAt(updatedAt)
                .updatedBy(updatedBy)
                .deletedAt(deletedAt)
                .deletedBy(deletedBy)
                .isDeleted(isDeleted)
                .build();
    }

    // ===== 비즈니스 메서드 =====

    /**
     * 배송 배정
     */
    public void assignDelivery(String deliveryId) {
        if (!this.status.canAssign()) {
            throw new LastMileDriverException(
                    LastMileDriverErrorCode.DRIVER_NOT_AVAILABLE,
                    String.format("배송 담당자가 배정 가능한 상태가 아닙니다. 현재 상태: %s",
                            this.status.getDescription())
            );
        }

        this.currentDeliveryId = deliveryId;
        this.status = LastMileDriverStatus.ON_DELIVERY;
        this.assignmentPriority++;

        log.info("배송 배정 - driverId: {}, deliveryId: {}, hubId: {}",
                this.getIdValue(), deliveryId, this.hubId);
    }

    /**
     * 배송 완료
     */
    public void completeDelivery(Long deliveryTimeMin) {
        if (this.status != LastMileDriverStatus.ON_DELIVERY) {
            throw new LastMileDriverException(
                    LastMileDriverErrorCode.DRIVER_NOT_ON_DELIVERY,
                    "배송 중이 아닌 담당자는 배송을 완료할 수 없습니다."
            );
        }

        // 통계 업데이트
        this.totalDeliveries++;
        if (deliveryTimeMin != null) {
            this.totalDeliveryTimeMin += deliveryTimeMin;
            this.averageDeliveryTimeMin = this.totalDeliveryTimeMin / this.totalDeliveries;
        }

        this.lastDeliveryCompletedAt = LocalDateTime.now();
        this.currentDeliveryId = null;
        this.status = LastMileDriverStatus.AVAILABLE;
        this.availableFrom = LocalDateTime.now();
        this.assignmentPriority = 0;

        log.info("배송 완료 - driverId: {}, hubId: {}, totalDeliveries: {}, avgTime: {}분",
                this.getIdValue(), this.hubId, this.totalDeliveries, this.averageDeliveryTimeMin);
    }

    /**
     * 배송 취소 (배정 해제)
     */
    public void cancelDelivery() {
        if (this.status != LastMileDriverStatus.ON_DELIVERY) {
            throw new LastMileDriverException(
                    LastMileDriverErrorCode.DRIVER_NOT_ON_DELIVERY,
                    "배송 중이 아닌 담당자는 배송을 취소할 수 없습니다."
            );
        }

        this.currentDeliveryId = null;
        this.status = LastMileDriverStatus.AVAILABLE;
        this.availableFrom = LocalDateTime.now();

        log.info("배송 취소 - driverId: {}, hubId: {}", this.getIdValue(), this.hubId);
    }

    /**
     * 근무 시작
     */
    public void startWork() {
        if (this.status == LastMileDriverStatus.OFF_DUTY) {
            this.status = LastMileDriverStatus.AVAILABLE;
            this.availableFrom = LocalDateTime.now();
            log.info("근무 시작 - driverId: {}, hubId: {}", this.getIdValue(), this.hubId);
        }
    }

    /**
     * 근무 종료
     */
    public void endWork() {
        if (this.status == LastMileDriverStatus.ON_DELIVERY) {
            throw new LastMileDriverException(
                    LastMileDriverErrorCode.DRIVER_ALREADY_ON_DELIVERY,
                    "배송 중인 담당자는 근무를 종료할 수 없습니다."
            );
        }
        this.status = LastMileDriverStatus.OFF_DUTY;
        log.info("근무 종료 - driverId: {}, hubId: {}", this.getIdValue(), this.hubId);
    }

    /**
     * 휴직 처리
     */
    public void deactivate() {
        if (this.status == LastMileDriverStatus.ON_DELIVERY) {
            throw new LastMileDriverException(
                    LastMileDriverErrorCode.DRIVER_ALREADY_ON_DELIVERY,
                    "배송 중인 담당자는 휴직 처리할 수 없습니다."
            );
        }
        this.status = LastMileDriverStatus.INACTIVE;
        log.info("휴직 처리 - driverId: {}, hubId: {}", this.getIdValue(), this.hubId);
    }

    /**
     * 복직 처리
     */
    public void activate() {
        if (this.status == LastMileDriverStatus.INACTIVE) {
            this.status = LastMileDriverStatus.AVAILABLE;
            this.availableFrom = LocalDateTime.now();
            log.info("복직 처리 - driverId: {}, hubId: {}", this.getIdValue(), this.hubId);
        }
    }

    // ===== Soft Delete =====

    public void delete(String deletedBy) {
        if (this.status == LastMileDriverStatus.ON_DELIVERY) {
            throw new LastMileDriverException(
                    LastMileDriverErrorCode.DRIVER_ALREADY_ON_DELIVERY,
                    "배송 중인 담당자는 삭제할 수 없습니다."
            );
        }
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    // ===== 검증 메서드 =====

    private static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new LastMileDriverException(
                    LastMileDriverErrorCode.INVALID_HUB_ID,
                    fieldName + "는 필수입니다."
            );
        }
    }

    // ===== 조회 메서드 =====

    public String getIdValue() {
        return this.id != null ? this.id.getValue() : null;
    }

    public boolean isAvailable() {
        return this.status == LastMileDriverStatus.AVAILABLE;
    }

    public boolean isOnDelivery() {
        return this.status == LastMileDriverStatus.ON_DELIVERY;
    }

    public boolean canWork() {
        return this.status.isWorking();
    }
}
