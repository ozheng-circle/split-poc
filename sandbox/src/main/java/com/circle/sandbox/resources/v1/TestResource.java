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

package com.circle.sandbox.resources.v1;

import com.circle.exceptions.CircleException;
import com.circle.logging.Level;
import com.circle.sandbox.codes.StatusCode;
import com.circle.sandbox.resources.v1.responses.TestResponseObject;
import com.circle.services.Versions;
import com.circle.services.resources.AbstractResource;
import com.circle.utils.MetricsUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Api("/v1")
@Path("/v1")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class TestResource extends AbstractResource {

    private static final String TAG_PROPERTY = "property";
    private static final String TAG_STATUS_CODE = "statusCode";

    private static final Logger LOGGER = LoggerFactory.getLogger("api");

    private static final String GET_VALUE_OK_METRIC =  "getValueOk";
    private static final String GET_VALUE_SERVER_ERROR_METRIC = "getValueServerError";

    @GET
    @Path("/test/{value}")
    @ApiOperation(
        value = "Returns the passed value",
        response = TestResponseObject.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Value successfully retrieved."),
        @ApiResponse(code = 400, message = "Invalid get request."),
        @ApiResponse(code = 404, message = "Value not found."),
        @ApiResponse(code = 500, message = "Internal server error.")
    })
    public Response getValue(@PathParam("value") int value) {
        try {
            TestResponseObject responseObject = new TestResponseObject();
            responseObject.setValue(value);
            responseObject.setStatus(buildSuccess());
            LOGGER.info("sandbox test endpoint gets called");

            increaseCounter(GET_VALUE_OK_METRIC);
            return ok(responseObject);
        } catch (Throwable ex) {
            reportError(GET_VALUE_SERVER_ERROR_METRIC, ex);
            throw serverError(buildError(ex), Level.WARN);
        }
    }

    @Override
    protected String getAPIVersion() {
        return Versions.V1_0;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @VisibleForTesting
    protected void reportError(String errorName, Throwable t) {
        try {

            ImmutableMap.Builder<String, String> tags = ImmutableMap.builder();
            if (t instanceof CircleException) {
                CircleException cex = (CircleException) t;
                if (cex.getStatusCode() != null) {
                    String statusCode = String.valueOf(cex.getStatusCode().getCode());
                    tags.put(TAG_STATUS_CODE, statusCode);
                }
                if (StringUtils.isNotBlank(cex.getProperty())) {
                    String property = StringUtils.deleteWhitespace(cex.getProperty());
                    tags.put(TAG_PROPERTY, property);
                }
            } else {
                tags.put(TAG_STATUS_CODE, String.valueOf(StatusCode.UNKNOWN_ERROR.getCode()));
            }

            String metricName = MetricsUtils.getMetricName("api", errorName, tags.build());
            increaseCounter(metricName);

        } catch (Throwable throwable) {
            LOGGER.warn("Unable to report error metric {}.", errorName, throwable);
        }
    }

    private void increaseCounter(String metricName) {
        try {
            MetricsUtils.increaseCounter(metricName);
        } catch (Throwable throwable) {
            LOGGER.warn("Unable to report metric {}.", metricName, throwable);
        }
    }
}
