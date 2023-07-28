/*
 * Copyright (c) 2023, Circle Internet Financial Trading Company Limited.
 * All rights reserved.
 *
 * Circle Internet Financial Trading Company Limited CONFIDENTIAL
 * This file includes unpublished proprietary source code of Circle Internet
 * Financial Trading Company Limited, Inc. The copyright notice above does not
 * evidence any actual or intended publication of such source code. Disclosure
 * of this source code or any related proprietary information is strictly
 * prohibited without the express written permission of Circle Internet Financial
 * Trading Company Limited.
 */

package com.circle.sandbox.codes;

import com.circle.codes.IStatusCode;
import com.circle.codes.common.CoreStatusCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Status codes used in the sandbox service.
 *
 * <p>Status codes are used to characterize possibly temporary and permanent errors in API responses or results.</p>
 */
public enum StatusCode implements IStatusCode {

    // Mixin the common status codes so other code only has to reference this enum.
    UNKNOWN_ERROR(CoreStatusCode.UNKNOWN_ERROR),
    SUCCESS(CoreStatusCode.SUCCESS),
    API_PARAMETER_MISSING(CoreStatusCode.API_PARAMETER_MISSING),
    API_PARAMETER_INVALID(CoreStatusCode.API_PARAMETER_INVALID),
    FORBIDDEN(CoreStatusCode.FORBIDDEN),
    RETRY(CoreStatusCode.RETRY),
    PENDING(CoreStatusCode.PENDING),
    INVALID_MESSAGE(CoreStatusCode.INVALID_MESSAGE);

    private static final Map<Integer, IStatusCode> STATUS_CODES_BY_CODE = new HashMap<>();

    static {
        for (StatusCode statusCode : values()) {
            STATUS_CODES_BY_CODE.put(statusCode.code, statusCode);
        }
    }

    private int code;
    private String description;
    private String detail;
    private String severity;

    StatusCode(IStatusCode statusCode) {
        this.code = statusCode.getCode();
        this.description = statusCode.getDescription();
        this.detail = statusCode.getDetail();
        this.severity = statusCode.getSeverity();
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    @Override
    public String getSeverity() {
        return severity;
    }
}
