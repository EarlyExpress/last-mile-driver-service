package com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal.dto.request;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.command.dto.LastMileDriverCommandDto.CreateCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 최종 배송 담당자 생성 요청 (Internal)
 * User Service → LastMileDriver Service
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LastMileDriverCreateRequest {

    @NotBlank(message = "사용자 ID는 필수입니다.")
    private String userId;

    @NotBlank(message = "허브 ID는 필수입니다.")
    private String hubId;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    /**
     * Request → Command 변환
     */
    public CreateCommand toCommand(String createdBy) {
        return CreateCommand.builder()
                .userId(this.userId)
                .hubId(this.hubId)
                .name(this.name)
                .createdBy(createdBy)
                .build();
    }

    public static LastMileDriverCreateRequest of(String userId, String hubId, String name) {
        return LastMileDriverCreateRequest.builder()
                .userId(userId)
                .hubId(hubId)
                .name(name)
                .build();
    }
}