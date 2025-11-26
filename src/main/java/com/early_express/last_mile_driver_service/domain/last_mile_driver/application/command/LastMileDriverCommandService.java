package com.early_express.last_mile_driver_service.domain.last_mile_driver.application.command;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.application.command.dto.LastMileDriverCommandDto.*;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.exception.LastMileDriverErrorCode;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.exception.LastMileDriverException;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.LastMileDriver;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverId;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.repository.LastMileDriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * LastMileDriver Command Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LastMileDriverCommandService {

    private final LastMileDriverRepository lastMileDriverRepository;

    /**
     * 드라이버 생성
     */
    public CreateResult create(CreateCommand command) {
        log.info("드라이버 생성 - userId: {}, hubId: {}, name: {}",
                command.getUserId(), command.getHubId(), command.getName());

        // 중복 체크
        if (lastMileDriverRepository.existsByUserId(command.getUserId())) {
            throw new LastMileDriverException(
                    LastMileDriverErrorCode.LAST_MILE_DRIVER_ALREADY_EXISTS,
                    "이미 등록된 배송 담당자입니다: " + command.getUserId()
            );
        }

        // 드라이버 생성
        LastMileDriver driver = LastMileDriver.create(
                command.getUserId(),
                command.getHubId(),
                command.getName(),
                command.getCreatedBy()
        );

        // 저장
        LastMileDriver savedDriver = lastMileDriverRepository.save(driver);

        log.info("드라이버 생성 완료 - driverId: {}, userId: {}, hubId: {}",
                savedDriver.getIdValue(), savedDriver.getUserId(), savedDriver.getHubId());

        return CreateResult.success(
                savedDriver.getIdValue(),
                savedDriver.getUserId(),
                savedDriver.getHubId(),
                savedDriver.getName(),
                savedDriver.getStatus().name()
        );
    }

    /**
     * 배송 자동 배정 (허브별)
     */
    public DriverAssignResult assignDelivery(AssignDeliveryCommand command) {
        log.info("배송 자동 배정 시작 - hubId: {}, deliveryId: {}",
                command.getHubId(), command.getDeliveryId());

        // 해당 허브의 배정 가능한 드라이버 조회
        List<LastMileDriver> availableDrivers =
                lastMileDriverRepository.findAvailableDriversByHubId(command.getHubId());

        if (availableDrivers.isEmpty()) {
            throw new LastMileDriverException(
                    LastMileDriverErrorCode.NO_AVAILABLE_DRIVER,
                    "해당 허브에 배정 가능한 배송 담당자가 없습니다: " + command.getHubId()
            );
        }

        // 첫 번째 드라이버 배정 (priority 가장 낮음)
        LastMileDriver driver = availableDrivers.get(0);
        driver.assignDelivery(command.getDeliveryId());

        // 저장
        lastMileDriverRepository.save(driver);

        log.info("배송 배정 완료 - driverId: {}, hubId: {}, deliveryId: {}, priority: {}",
                driver.getIdValue(), driver.getHubId(), command.getDeliveryId(), driver.getAssignmentPriority());

        return DriverAssignResult.success(
                driver.getIdValue(),
                driver.getUserId(),
                driver.getHubId(),
                driver.getName(),
                driver.getStatus().name()
        );
    }

    /**
     * 배송 완료 처리
     */
    public void completeDelivery(CompleteDeliveryCommand command) {
        log.info("배송 완료 처리 - driverId: {}, deliveryTime: {}분",
                command.getDriverId(), command.getDeliveryTimeMin());

        LastMileDriver driver = findDriver(command.getDriverId());

        // 배송 완료
        driver.completeDelivery(command.getDeliveryTimeMin());

        // 저장
        lastMileDriverRepository.save(driver);

        log.info("배송 완료 - driverId: {}, hubId: {}, totalDeliveries: {}, avgTime: {}분",
                driver.getIdValue(), driver.getHubId(), driver.getTotalDeliveries(), driver.getAverageDeliveryTimeMin());
    }

    /**
     * 배송 취소 (배정 해제)
     */
    public void cancelDelivery(CancelDeliveryCommand command) {
        log.info("배송 취소 처리 - driverId: {}", command.getDriverId());

        LastMileDriver driver = findDriver(command.getDriverId());

        // 배송 취소
        driver.cancelDelivery();

        // 저장
        lastMileDriverRepository.save(driver);

        log.info("배송 취소 완료 - driverId: {}, hubId: {}", driver.getIdValue(), driver.getHubId());
    }

    /**
     * 근무 시작
     */
    public void startWork(StartWorkCommand command) {
        log.info("근무 시작 - driverId: {}", command.getDriverId());

        LastMileDriver driver = findDriver(command.getDriverId());

        driver.startWork();

        lastMileDriverRepository.save(driver);

        log.info("근무 시작 완료 - driverId: {}, hubId: {}", driver.getIdValue(), driver.getHubId());
    }

    /**
     * 근무 종료
     */
    public void endWork(EndWorkCommand command) {
        log.info("근무 종료 - driverId: {}", command.getDriverId());

        LastMileDriver driver = findDriver(command.getDriverId());

        driver.endWork();

        lastMileDriverRepository.save(driver);

        log.info("근무 종료 완료 - driverId: {}, hubId: {}", driver.getIdValue(), driver.getHubId());
    }

    /**
     * 휴직 처리
     */
    public void deactivate(DeactivateCommand command) {
        log.info("휴직 처리 - driverId: {}", command.getDriverId());

        LastMileDriver driver = findDriver(command.getDriverId());

        driver.deactivate();

        lastMileDriverRepository.save(driver);

        log.info("휴직 처리 완료 - driverId: {}, hubId: {}", driver.getIdValue(), driver.getHubId());
    }

    /**
     * 복직 처리
     */
    public void activate(ActivateCommand command) {
        log.info("복직 처리 - driverId: {}", command.getDriverId());

        LastMileDriver driver = findDriver(command.getDriverId());

        driver.activate();

        lastMileDriverRepository.save(driver);

        log.info("복직 처리 완료 - driverId: {}, hubId: {}", driver.getIdValue(), driver.getHubId());
    }

    // ===== Private Methods =====

    private LastMileDriver findDriver(String driverId) {
        return lastMileDriverRepository.findById(LastMileDriverId.of(driverId))
                .orElseThrow(() -> new LastMileDriverException(
                        LastMileDriverErrorCode.LAST_MILE_DRIVER_NOT_FOUND,
                        "최종 배송 담당자를 찾을 수 없습니다: " + driverId
                ));
    }
}