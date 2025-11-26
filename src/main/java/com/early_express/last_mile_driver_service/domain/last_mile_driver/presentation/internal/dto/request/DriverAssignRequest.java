package com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal.dto.request;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.command.dto.LastMileDriverCommandDto.AssignDeliveryCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 드라이버 자동 배정 요청 (Internal)
 * LastMileDelivery Service → LastMileDriver Service
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverAssignRequest {

    @NotBlank(message = "허브 ID는 필수입니다.")
    private String hubId;

    @NotBlank(message = "배송 ID는 필수입니다.")
    private String deliveryId;

    /**
     * Request → Command 변환
     */
    public AssignDeliveryCommand toCommand() {
        return AssignDeliveryCommand.builder()
                .hubId(this.hubId)
                .deliveryId(this.deliveryId)
                .build();
    }

    public static DriverAssignRequest of(String hubId, String deliveryId) {
        return DriverAssignRequest.builder()
                .hubId(hubId)
                .deliveryId(deliveryId)
                .build();
    }
}