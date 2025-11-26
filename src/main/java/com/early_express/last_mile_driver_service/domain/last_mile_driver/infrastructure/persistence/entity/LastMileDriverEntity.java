package com.early_express.last_mile_driver_service.domain.last_mile_driver.infrastructure.persistence.entity;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.LastMileDriver;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverId;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverStatus;
import com.early_express.last_mile_driver_service.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * LastMileDriver JPA Entity
 */
@Entity
@Table(name = "p_last_mile_driver", indexes = {
        @Index(name = "idx_last_mile_driver_user_id", columnList = "user_id", unique = true),
        @Index(name = "idx_last_mile_driver_hub_id", columnList = "hub_id"),
        @Index(name = "idx_last_mile_driver_status", columnList = "hub_id, status"),
        @Index(name = "idx_last_mile_driver_priority", columnList = "hub_id, assignment_priority, available_from")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LastMileDriverEntity extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, unique = true, length = 36)
    private String userId;

    @Column(name = "hub_id", nullable = false, length = 36)
    private String hubId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LastMileDriverStatus status;

    @Column(name = "current_delivery_id", length = 36)
    private String currentDeliveryId;

    @Column(name = "assignment_priority", nullable = false)
    private Integer assignmentPriority;

    @Column(name = "total_deliveries", nullable = false)
    private Long totalDeliveries;

    @Column(name = "total_delivery_time_min", nullable = false)
    private Long totalDeliveryTimeMin;

    @Column(name = "average_delivery_time_min", nullable = false)
    private Long averageDeliveryTimeMin;

    @Column(name = "last_delivery_completed_at")
    private LocalDateTime lastDeliveryCompletedAt;

    @Column(name = "available_from")
    private LocalDateTime availableFrom;

    @Builder
    private LastMileDriverEntity(String id, String userId, String hubId, String name,
                                 LastMileDriverStatus status, String currentDeliveryId,
                                 Integer assignmentPriority, Long totalDeliveries,
                                 Long totalDeliveryTimeMin, Long averageDeliveryTimeMin,
                                 LocalDateTime lastDeliveryCompletedAt, LocalDateTime availableFrom) {
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
    }

    // ===== 도메인 → 엔티티 변환 =====

    public static LastMileDriverEntity fromDomain(LastMileDriver driver) {
        String entityId = driver.getIdValue() != null
                ? driver.getIdValue()
                : UUID.randomUUID().toString();

        return LastMileDriverEntity.builder()
                .id(entityId)
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
                .build();
    }

    // ===== 엔티티 → 도메인 변환 =====

    public LastMileDriver toDomain() {
        return LastMileDriver.reconstitute(
                LastMileDriverId.of(this.id),
                this.userId,
                this.hubId,
                this.name,
                this.status,
                this.currentDeliveryId,
                this.assignmentPriority,
                this.totalDeliveries,
                this.totalDeliveryTimeMin,
                this.averageDeliveryTimeMin,
                this.lastDeliveryCompletedAt,
                this.availableFrom,
                this.getCreatedAt(),
                this.getCreatedBy(),
                this.getUpdatedAt(),
                this.getUpdatedBy(),
                this.getDeletedAt(),
                this.getDeletedBy(),
                this.isDeleted()
        );
    }

    // ===== 도메인 → 엔티티 업데이트 =====

    public void updateFromDomain(LastMileDriver driver) {
        if (!this.id.equals(driver.getIdValue())) {
            throw new IllegalStateException(
                    "엔티티 ID와 도메인 ID가 일치하지 않습니다. " +
                            "Entity ID: " + this.id + ", Domain ID: " + driver.getIdValue()
            );
        }

        this.hubId = driver.getHubId();
        this.name = driver.getName();
        this.status = driver.getStatus();
        this.currentDeliveryId = driver.getCurrentDeliveryId();
        this.assignmentPriority = driver.getAssignmentPriority();
        this.totalDeliveries = driver.getTotalDeliveries();
        this.totalDeliveryTimeMin = driver.getTotalDeliveryTimeMin();
        this.averageDeliveryTimeMin = driver.getAverageDeliveryTimeMin();
        this.lastDeliveryCompletedAt = driver.getLastDeliveryCompletedAt();
        this.availableFrom = driver.getAvailableFrom();
    }
}