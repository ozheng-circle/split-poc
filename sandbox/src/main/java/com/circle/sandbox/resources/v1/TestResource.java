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
import com.circle.logging.CircleLogger;
import com.circle.logging.Level;
import com.circle.sandbox.client.CircleSplitClient;
import com.circle.sandbox.codes.StatusCode;
import com.circle.sandbox.resources.v1.responses.TestResponseObject;
import com.circle.services.Versions;
import com.circle.services.resources.AbstractResource;
import com.circle.utils.MetricsUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import io.split.client.SplitClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Api("/v1")
@Path("/v1")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class TestResource extends AbstractResource {

    private static final String TAG_PROPERTY = "property";
    private static final String TAG_STATUS_CODE = "statusCode";

    private static final CircleLogger LOGGER = new CircleLogger("api");

    private static final String GET_VALUE_OK_METRIC =  "getValueOk";
    private static final String GET_VALUE_SERVER_ERROR_METRIC = "getValueServerError";

    @GET
    @Path("/whitelist")
    @ApiOperation(
        value = "Returns the passed value",
        response = TestResponseObject.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Value successfully retrieved."),
        @ApiResponse(code = 400, message = "Invalid get request."),
        @ApiResponse(code = 404, message = "Value not found."),
        @ApiResponse(code = 500, message = "Internal server error.")
    })
    public Response testWhiteList() {
        try {
            SplitClient splitClient = CircleSplitClient.getInstance().getSplitClient();

            /*
            Targeting based on attributes. Requires traffic type to have these attributes.
            In this case, the User traffic type has the attributes "email, balance, name, and entity_id"
            The targeting rule matches for email of "oliver.zheng@circle.com" and a balance over 300
             */
            Map<String, Object> attributes = Map.of(
                "email", "oliver.zheng@circle.com",
                "balance", 200
            );

            // Matching just by the User traffic type, with key=user_1
            //String treatment = splitClient.getTreatment("user_1", "Whitelist_Feature");

            // Matching by just attributes because user_2 is not an individual target
            String treatment = splitClient.getTreatment("user_2", "Whitelist_Feature", attributes);

            if (treatment.equals("on")) {
                LOGGER.info("User is in white_list");
            } else if (treatment.equals("off")) {
                LOGGER.info("User is not in white list");
            } else {
                LOGGER.info("Something went wrong trying to read treatment.");
            }

            TestResponseObject<String> responseObject = new TestResponseObject<>();
            responseObject.setValue(String.format("User is receiving %s treatment.",treatment));
            responseObject.setStatus(buildSuccess());

            increaseCounter(GET_VALUE_OK_METRIC);
            return ok(responseObject);
        } catch (Throwable ex) {
            reportError(GET_VALUE_SERVER_ERROR_METRIC, ex);
            throw serverError(buildError(ex), Level.WARN);
        }
    }

    /*
    Per Split engineer:
    If you use a percentage distribution, each key is hashed with a unique identifier for the flag.
    The hash is bucketed from 0-99.  So each key has a bucket number.  The SDK distributes according
    to the rules.  If "on" is 15%, "off" 85%, then those keys bucketed from 0-14 will be "on".
    Multiple treatments work the same way, and the bucketing flows from top to bottom.
     */

    @GET
    @Path("/percentage")
    @ApiOperation(
        value = "Returns the passed value",
        response = TestResponseObject.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Value successfully retrieved."),
        @ApiResponse(code = 400, message = "Invalid get request."),
        @ApiResponse(code = 404, message = "Value not found."),
        @ApiResponse(code = 500, message = "Internal server error.")
    })
    public Response testPercentageRollout() {
        try {
            SplitClient splitClient = CircleSplitClient.getInstance().getSplitClient();
            String treatment = splitClient.getTreatment("user_random", "Percentage_Rollout_Feature");

            TestResponseObject<String> responseObject = new TestResponseObject<>();
            responseObject.setValue(String.format("User is receiving %s treatment.",treatment));
            responseObject.setStatus(buildSuccess());

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
        return LOGGER.getLogger();
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
            //LOGGER.warn("Unable to report error metric {}.", errorName, throwable);
        }
    }

    private void increaseCounter(String metricName) {
        try {
            MetricsUtils.increaseCounter(metricName);
        } catch (Throwable throwable) {
            //LOGGER.warn("Unable to report metric {}.", metricName, throwable);
        }
    }
}
