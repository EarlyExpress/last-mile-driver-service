package com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal.dto.request;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.command.dto.LastMileDriverCommandDto.CompleteDeliveryCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배송 완료 통지 요청 (Internal)
 * LastMileDelivery Service → LastMileDriver Service
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverCompleteRequest {

    private Long deliveryTimeMin;

    /**
     * Request → Command 변환
     */
    public CompleteDeliveryCommand toCommand(String driverId) {
        return CompleteDeliveryCommand.builder()
                .driverId(driverId)
                .deliveryTimeMin(this.deliveryTimeMin)
                .build();
    }

    public static DriverCompleteRequest of(Long deliveryTimeMin) {
        return DriverCompleteRequest.builder()
                .deliveryTimeMin(deliveryTimeMin)
                .build();
    }
}