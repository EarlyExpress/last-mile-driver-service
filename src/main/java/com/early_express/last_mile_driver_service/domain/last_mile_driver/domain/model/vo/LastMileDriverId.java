package com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * LastMileDriver ID 값 객체
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LastMileDriverId {

    private String value;

    private LastMileDriverId(String value) {
        this.value = value;
    }

    public static LastMileDriverId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LastMileDriverId는 null이거나 빈 값일 수 없습니다.");
        }
        return new LastMileDriverId(value);
    }
}