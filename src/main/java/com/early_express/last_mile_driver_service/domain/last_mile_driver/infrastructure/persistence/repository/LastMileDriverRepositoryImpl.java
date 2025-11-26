package com.early_express.last_mile_driver_service.domain.last_mile_driver.infrastructure.persistence.repository;

import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.exception.LastMileDriverErrorCode;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.exception.LastMileDriverException;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.LastMileDriver;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverId;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.model.vo.LastMileDriverStatus;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.repository.LastMileDriverRepository;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.infrastructure.persistence.entity.LastMileDriverEntity;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.infrastructure.persistence.entity.QLastMileDriverEntity;
import com.early_express.last_mile_driver_service.domain.last_mile_driver.infrastructure.persistence.jpa.LastMileDriverJpaRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * LastMileDriver Repository 구현체
 */
@Repository
@RequiredArgsConstructor
public class LastMileDriverRepositoryImpl implements LastMileDriverRepository {

    private final LastMileDriverJpaRepository jpaRepository;
    private final JPAQueryFactory queryFactory;

    private static final QLastMileDriverEntity driver = QLastMileDriverEntity.lastMileDriverEntity;

    @Override
    @Transactional
    public LastMileDriver save(LastMileDriver lastMileDriver) {
        LastMileDriverEntity entity;

        if (lastMileDriver.getId() != null) {
            entity = jpaRepository.findByIdAndIsDeletedFalse(lastMileDriver.getIdValue())
                    .orElseThrow(() -> new LastMileDriverException(
                            LastMileDriverErrorCode.LAST_MILE_DRIVER_NOT_FOUND,
                            "최종 배송 담당자를 찾을 수 없습니다: " + lastMileDriver.getIdValue()
                    ));
            entity.updateFromDomain(lastMileDriver);
        } else {
            entity = LastMileDriverEntity.fromDomain(lastMileDriver);
            entity = jpaRepository.save(entity);
        }

        return entity.toDomain();
    }

    @Override
    public Optional<LastMileDriver> findById(LastMileDriverId id) {
        return jpaRepository.findByIdAndIsDeletedFalse(id.getValue())
                .map(LastMileDriverEntity::toDomain);
    }

    @Override
    public Optional<LastMileDriver> findByUserId(String userId) {
        return jpaRepository.findByUserIdAndIsDeletedFalse(userId)
                .map(LastMileDriverEntity::toDomain);
    }

    @Override
    public List<LastMileDriver> findAvailableDriversByHubId(String hubId) {
        List<LastMileDriverEntity> entities = queryFactory
                .selectFrom(driver)
                .where(
                        hubIdEq(hubId),
                        statusEq(LastMileDriverStatus.AVAILABLE),
                        isNotDeleted()
                )
                .orderBy(
                        driver.assignmentPriority.asc(),
                        driver.availableFrom.asc()
                )
                .fetch();

        return entities.stream()
                .map(LastMileDriverEntity::toDomain)
                .toList();
    }

    @Override
    public Page<LastMileDriver> findByHubId(String hubId, Pageable pageable) {
        return jpaRepository.findByHubIdAndIsDeletedFalse(hubId, pageable)
                .map(LastMileDriverEntity::toDomain);
    }

    @Override
    public Page<LastMileDriver> findByHubIdAndStatus(String hubId, LastMileDriverStatus status, Pageable pageable) {
        return jpaRepository.findByHubIdAndStatusAndIsDeletedFalse(hubId, status, pageable)
                .map(LastMileDriverEntity::toDomain);
    }

    @Override
    public Page<LastMileDriver> findAll(Pageable pageable) {
        return jpaRepository.findByIsDeletedFalse(pageable)
                .map(LastMileDriverEntity::toDomain);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return jpaRepository.existsByUserIdAndIsDeletedFalse(userId);
    }

    // ===== BooleanExpression =====

    private BooleanExpression hubIdEq(String hubId) {
        return hubId != null ? driver.hubId.eq(hubId) : null;
    }

    private BooleanExpression statusEq(LastMileDriverStatus status) {
        return status != null ? driver.status.eq(status) : null;
    }

    private BooleanExpression isNotDeleted() {
        return driver.isDeleted.eq(false);
    }
}