package com.early_express.last_mile_driver_service.domain.last_mile_driver.domain.exception;

import com.early_express.last_mile_driver_service.global.presentation.exception.GlobalException;

/**
 * LastMileDriver 도메인 예외
 */
public class LastMileDriverException extends GlobalException {

    public LastMileDriverException(LastMileDriverErrorCode errorCode) {
        super(errorCode);
    }

    public LastMileDriverException(LastMileDriverErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public LastMileDriverException(LastMileDriverErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public LastMileDriverException(LastMileDriverErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
