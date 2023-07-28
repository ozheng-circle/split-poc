package com.circle.sandbox.resources.v1;

import com.circle.exceptions.ClientException;
import com.circle.exceptions.ServerException;
import com.circle.sandbox.codes.StatusCode;
import com.circle.sandbox.resources.v1.responses.TestResponseObject;
import com.circle.services.responses.ResponseWrapperObject;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TestResourceTest {
    @InjectMocks
    private TestResource resource;

    @Before
    public void setUp() {
        SharedMetricRegistries.clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getValue_success() {
        Response actualResponse = resource.getValue(5);

        TestResponseObject expectedResponseObject = new TestResponseObject();
        expectedResponseObject.setValue(5);

        assertThat(actualResponse.getEntity()).isInstanceOf(ResponseWrapperObject.class);

        TestResponseObject actualResponseObject =
            ((ResponseWrapperObject<TestResponseObject>) actualResponse.getEntity()).getResponse();
        assertThat(actualResponseObject.getValue()).isEqualTo(5);

        MetricRegistry registry = SharedMetricRegistries.getOrCreate("metrics-registry");
        assertThat(registry.getCounters().get("getValueOk").getCount()).isEqualTo(1L);
    }

    @Test
    public void reportError_withStatusCodeAndPropertyName() {
        StatusCode statusCode = StatusCode.API_PARAMETER_INVALID;
        String propertyName = "issuerIdentifier";
        String metricName = RandomStringUtils.randomAlphabetic(10);

        ClientException exception = new ClientException(
            "Oops! Something went wrong!", propertyName, statusCode);

        resource.reportError(metricName, exception);

        MetricRegistry registry = SharedMetricRegistries.getOrCreate("metrics-registry");
        assertThat(registry.getCounters().keySet()).contains(String.format(
            "api.%s['statusCode:%s','property:%s']", metricName, statusCode.getCode(), propertyName));
    }

    @Test
    public void reportError_noStatusCode() {
        String metricName = RandomStringUtils.randomAlphabetic(10);

        ServerException exception = new ServerException("Oops! Something went wrong!");

        resource.reportError(metricName, exception);

        MetricRegistry registry = SharedMetricRegistries.getOrCreate("metrics-registry");
        assertThat(registry.getCounters().keySet()).contains(String.format(
            "api.%s['statusCode:%s']", metricName, StatusCode.UNKNOWN_ERROR.getCode()));
    }

    @Test
    public void reportError_noCircleException() {
        String metricName = RandomStringUtils.randomAlphabetic(10);

        RuntimeException exception = new RuntimeException("Oops! Something went wrong!");

        resource.reportError(metricName, exception);

        MetricRegistry registry = SharedMetricRegistries.getOrCreate("metrics-registry");
        assertThat(registry.getCounters().keySet()).contains(String.format(
            "api.%s['statusCode:%s']", metricName, StatusCode.UNKNOWN_ERROR.getCode()));
    }
}
