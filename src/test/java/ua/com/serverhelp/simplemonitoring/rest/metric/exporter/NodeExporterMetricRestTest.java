package ua.com.serverhelp.simplemonitoring.rest.metric.exporter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.com.serverhelp.simplemonitoring.AbstractTest;

import java.io.InputStream;

class NodeExporterMetricRestTest extends AbstractTest {

    @BeforeEach
    void setUp2() throws Exception {
        registerTestUsers();
        createOrganization();

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream debianInput = classLoader.getResourceAsStream("exporter/node/metrics.bin");
        Assertions.assertNotNull(debianInput);
        byte[] metrics = debianInput.readAllBytes();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/metric/exporter/node/")
                        .header("X-Simple-Token", accessToken.getId())
                        .header("X-Project", "testproj")
                        .header("X-Hostname", "debian")
                        .content(metrics)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
        //Click
        InputStream clickInput = classLoader.getResourceAsStream("exporter/node/click.bin");
        Assertions.assertNotNull(clickInput);
        byte[] clickMetrics = clickInput.readAllBytes();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/metric/exporter/node/")
                        .header("X-Simple-Token", accessToken.getId())
                        .header("X-Project", "testproj")
                        .header("X-Hostname", "click")
                        .content(clickMetrics)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
        //Stg
        InputStream stgInput = classLoader.getResourceAsStream("exporter/node/stg_srv.bin");
        Assertions.assertNotNull(stgInput);
        byte[] stgMetrics = stgInput.readAllBytes();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/metric/exporter/node/")
                        .header("X-Simple-Token", accessToken.getId())
                        .header("X-Project", "testproj")
                        .header("X-Hostname", "stg")
                        .content(stgMetrics)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
    }

    @Test
    void receiveData() {
    }
}