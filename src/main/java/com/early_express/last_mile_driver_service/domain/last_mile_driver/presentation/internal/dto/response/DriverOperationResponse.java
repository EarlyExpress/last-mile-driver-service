package com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 드라이버 작업 응답 (Internal)
 * 완료/취소 등의 작업 결과
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverOperationResponse {

    private String driverId;
    private String status;
    private String message;
    private Long timestamp;

    public static DriverOperationResponse success(String driverId, String status, String message) {
        return DriverOperationResponse.builder()
                .driverId(driverId)
                .status(status)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static DriverOperationResponse completed(String driverId) {
        return success(driverId, "AVAILABLE", "배송이 완료되었습니다.");
    }

    public static DriverOperationResponse cancelled(String driverId) {
        return success(driverId, "AVAILABLE", "배송이 취소되었습니다.");
    }
}