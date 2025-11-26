package com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.web.master;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.query.LastMileDriverQueryService;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.query.dto.LastMileDriverQueryDto.LastMileDriverResponse;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.presentation.web.master.dto.response.MasterLastMileDriverResponse;
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
 * Master LastMileDriver Controller
 * 마스터 관리자용 드라이버 조회 API
 */
@Slf4j
@RestController
@RequestMapping("/v1/last-mile-driver/web/master")
@RequiredArgsConstructor
public class LastMileDriverMasterController {

    private final LastMileDriverQueryService queryService;

    /**
     * 전체 드라이버 목록 조회
     * GET /v1/last-mile-driver/web/master/drivers
     */
    @GetMapping("/drivers")
    public ApiResponse<PageResponse<MasterLastMileDriverResponse>> getAllDrivers(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Roles") String roles,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        log.info("마스터 드라이버 목록 조회");

        // TODO: roles 검증 (MASTER 권한 확인)

        Page<LastMileDriverResponse> queryResult = queryService.findAll(pageable);

        List<MasterLastMileDriverResponse> content = queryResult.getContent().stream()
                .map(MasterLastMileDriverResponse::from)
                .toList();

        return ApiResponse.success(PageResponse.of(content, PageInfo.of(queryResult)));
    }
}