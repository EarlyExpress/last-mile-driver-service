package com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 최종 배송 담당자 상태
 */
@Getter
@RequiredArgsConstructor
public enum LastMileDriverStatus {

    AVAILABLE("배정 가능"),
    ON_DELIVERY("배송 중"),
    OFF_DUTY("비활성"),
    INACTIVE("휴직");

    private final String description;

    public boolean canAssign() {
        return this == AVAILABLE;
    }

    public boolean isWorking() {
        return this == AVAILABLE || this == ON_DELIVERY;
    }
}