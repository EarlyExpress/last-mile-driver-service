package com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal.dto.response;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.command.dto.LastMileDriverCommandDto.CreateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 최종 배송 담당자 생성 응답 (Internal)
 * LastMileDriver Service → User Service
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LastMileDriverCreateResponse {

    private String driverId;
    private String userId;
    private String hubId;
    private String name;
    private String status;

    /**
     * Command Result → Response 변환
     */
    public static LastMileDriverCreateResponse from(CreateResult result) {
        return LastMileDriverCreateResponse.builder()
                .driverId(result.getDriverId())
                .userId(result.getUserId())
                .hubId(result.getHubId())
                .name(result.getName())
                .status(result.getStatus())
                .build();
    }
}
