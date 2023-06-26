package ua.com.serverhelp.simplemonitoring.rest.metric.exporter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.com.serverhelp.simplemonitoring.AbstractTest;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class BlackboxExporterMetricRestTest extends AbstractTest {

    @BeforeEach
    void setUp2() throws Exception {
        registerTestUsers();
        createOrganization();

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream debianInput = classLoader.getResourceAsStream("exporter/blackbox/blackbox_response");
        Assertions.assertNotNull(debianInput);
        byte[] metrics = debianInput.readAllBytes();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/metric/exporter/blackbox/")
                        .header("X-Simple-Token", accessToken.getId())
                        .header("X-Project", "testproj")
                        .header("X-Site-Id", "https_example_com")
                        .content(metrics)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
    }

    @Test
    void receiveData() {
        var triggers=triggerRepository.findAll();
        Assertions.assertEquals(2, triggers.size());
    }
}