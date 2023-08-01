package com.circle.sandbox.resources.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.split.client.SplitClient;
import io.split.client.SplitClientConfig;
import io.split.client.SplitFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class TestResourceTest {
    @InjectMocks
    private TestResource resource;

    private SplitClient client;

    @Before
    public void setUp() {
        SplitClientConfig config = SplitClientConfig.builder()
            .splitFile("./local-flags.yaml")
            .setBlockUntilReadyTimeout(10000)
            .localhostRefreshEnable(true)
            .featuresRefreshRate(5)
            .build();

        try{
            client = SplitFactoryBuilder.build("localhost", config).client();
            client.blockUntilReady();
        }catch (Exception e) {
            throw new RuntimeException("Failed to build local split client");
        }
    }

    @Test
    public void testUpdateFlag_readFlag_flagUpdated () {

        String treatment = client.getTreatment("testUser", "Feature_Flag");
        assertThat(treatment).isEqualTo("on");

        updateFlag("Feature_Flag", "off");

        treatment = client.getTreatment("testUser", "Feature_Flag");
        assertThat(treatment).isEqualTo("off");

        updateFlag("Feature_Flag", "on");
    }

    public static void updateFlag(String flagName,  String newTreatment) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            JsonNode root = mapper.readTree(new File("./local-flags.yaml"));
            for (JsonNode flag : root){
                if (flag.has(flagName)){
                    ObjectNode newFlag = (ObjectNode)flag.path(flagName);
                    newFlag.put("treatment", newTreatment);
                }
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("./local-flags.yaml"), root);
            Thread.sleep(5000);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error reading or writing flag file: " + e.getMessage());
        }
    }
}
