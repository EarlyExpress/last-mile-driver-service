package com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.web.hubmanager;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.query.LastMileDriverQueryService;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.query.dto.LastMileDriverQueryDto.LastMileDriverResponse;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverStatus;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.web.hubmanager.dto.response.HubManagerLastMileDriverResponse;
import com.early_express.last_mile_driver_service.global.common.dto.PageInfo;
import com.early_express.last_mile_driver_service.global.presentation.dto.ApiResponse;
import com.early_express.last_mile_driver_service.global.presentation.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * HubManager LastMileDriver Controller
 * 허브 관리자용 드라이버 조회 API
 */
@Slf4j
@RestController
@RequestMapping("/v1/last-mile-driver/web/hub-manager")
@RequiredArgsConstructor
public class LastMileDriverHubManagerController {

    private final LastMileDriverQueryService queryService;

    /**
     * 허브별 드라이버 목록 조회
     * GET /v1/last-mile-driver/web/hub-manager/drivers
     */
    @GetMapping("/drivers")
    public ApiResponse<PageResponse<HubManagerLastMileDriverResponse>> getHubDrivers(
            @RequestParam String hubId,
            @RequestParam(required = false) LastMileDriverStatus status,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Roles") String roles,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        log.info("허브 관리자 드라이버 목록 조회 - hubId: {}, status: {}", hubId, status);

        // TODO: roles 검증 (HUB_MANAGER 권한 확인)
        // TODO: hubId 권한 확인 (자신의 허브만 조회 가능)

        Page<LastMileDriverResponse> queryResult = status != null
                ? queryService.findByHubIdAndStatus(hubId, status, pageable)
                : queryService.findByHubId(hubId, pageable);

        List<HubManagerLastMileDriverResponse> content = queryResult.getContent().stream()
                .map(HubManagerLastMileDriverResponse::from)
                .toList();

        return ApiResponse.success(PageResponse.of(content, PageInfo.of(queryResult)));
    }
}