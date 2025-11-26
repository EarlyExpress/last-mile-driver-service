package com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.command.LastMileDriverCommandService;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.command.dto.LastMileDriverCommandDto.*;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal.dto.request.DriverAssignRequest;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal.dto.request.DriverCancelRequest;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal.dto.request.DriverCompleteRequest;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal.dto.request.LastMileDriverCreateRequest;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal.dto.response.DriverAssignResponse;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal.dto.response.DriverOperationResponse;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.internal.dto.response.LastMileDriverCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * LastMileDriver Internal Controller
 * 내부 서비스 간 통신용 (LastMileDelivery Service)
 */
@Slf4j
@RestController
@RequestMapping("/v1/last-mile-driver/internal")
@RequiredArgsConstructor
public class LastMileDriverInternalController {

    private final LastMileDriverCommandService lastMileDriverCommandService;

    /**
     * 최종 배송 담당자 생성
     * POST /v1/last-mile-driver/internal/drivers
     *
     * User Service에서 최종 배송 담당자 등록 시 호출
     */
    @PostMapping("/drivers")
    public LastMileDriverCreateResponse createDriver(
            @Valid @RequestBody LastMileDriverCreateRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        log.info("[Internal] 최종 배송 담당자 생성 요청 - userId: {}, hubId: {}, name: {}",
                request.getUserId(), request.getHubId(), request.getName());

        CreateCommand command = request.toCommand(userId);
        CreateResult result = lastMileDriverCommandService.create(command);

        log.info("[Internal] 최종 배송 담당자 생성 완료 - driverId: {}, userId: {}, hubId: {}",
                result.getDriverId(), result.getUserId(), result.getHubId());

        return LastMileDriverCreateResponse.from(result);
    }

    /**
     * 드라이버 자동 배정 (허브별)
     * POST /v1/last-mile-driver/internal/drivers/assign
     *
     * LastMileDelivery 생성 시 호출
     * 해당 허브 소속 드라이버 중 우선순위가 가장 낮은 드라이버에게 자동 배정
     */
    @PostMapping("/drivers/assign")
    public DriverAssignResponse assignDriver(@Valid @RequestBody DriverAssignRequest request) {
        log.info("[Internal] 드라이버 자동 배정 요청 - hubId: {}, deliveryId: {}",
                request.getHubId(), request.getDeliveryId());

        AssignDeliveryCommand command = request.toCommand();
        DriverAssignResult result = lastMileDriverCommandService.assignDelivery(command);

        log.info("[Internal] 드라이버 배정 완료 - driverId: {}, hubId: {}, deliveryId: {}",
                result.getDriverId(), result.getHubId(), request.getDeliveryId());

        return DriverAssignResponse.from(result);
    }

    /**
     * 배송 완료 통지
     * PUT /v1/last-mile-driver/internal/drivers/{driverId}/complete
     *
     * LastMileDelivery 완료 시 호출
     * 드라이버 상태를 AVAILABLE로 변경하고 통계 업데이트
     */
    @PutMapping("/drivers/{driverId}/complete")
    public DriverOperationResponse completeDelivery(
            @PathVariable String driverId,
            @RequestBody DriverCompleteRequest request) {

        log.info("[Internal] 배송 완료 통지 - driverId: {}, deliveryTime: {}분",
                driverId, request.getDeliveryTimeMin());

        CompleteDeliveryCommand command = request.toCommand(driverId);
        lastMileDriverCommandService.completeDelivery(command);

        log.info("[Internal] 배송 완료 처리 완료 - driverId: {}", driverId);

        return DriverOperationResponse.completed(driverId);
    }

    /**
     * 배송 취소 통지
     * PUT /v1/last-mile-driver/internal/drivers/{driverId}/cancel
     *
     * LastMileDelivery 취소 시 호출
     * 드라이버 배정 해제 및 상태를 AVAILABLE로 변경
     */
    @PutMapping("/drivers/{driverId}/cancel")
    public DriverOperationResponse cancelDelivery(
            @PathVariable String driverId,
            @RequestBody(required = false) DriverCancelRequest request) {

        log.info("[Internal] 배송 취소 통지 - driverId: {}", driverId);

        CancelDeliveryCommand command = CancelDeliveryCommand.builder()
                .driverId(driverId)
                .build();

        lastMileDriverCommandService.cancelDelivery(command);

        log.info("[Internal] 배송 취소 처리 완료 - driverId: {}", driverId);

        return DriverOperationResponse.cancelled(driverId);
    }
}