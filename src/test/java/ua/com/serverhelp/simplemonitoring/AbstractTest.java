package ua.com.serverhelp.simplemonitoring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.instancio.Instancio;
import org.instancio.generator.Generator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.serverhelp.simplemonitoring.api.auth.type.RegisterRequest;
import ua.com.serverhelp.simplemonitoring.entity.accesstoken.AccessToken;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.entity.user.Role;
import ua.com.serverhelp.simplemonitoring.repository.*;
import ua.com.serverhelp.simplemonitoring.service.AuthenticationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.Select.field;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected AuthenticationService authenticationService;
    @Autowired
    protected TokenRepository tokenRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected OrganizationRepository organizationRepository;
    @Autowired
    protected ParameterGroupRepository parameterGroupRepository;
    @Autowired
    protected MetricRepository metricRepository;
    @Autowired
    protected AccessTokenRepository accessTokenRepository;
    protected AccessToken accessToken;


    @AfterEach
    void tearDown() {
        accessTokenRepository.deleteAll();
        tokenRepository.deleteAll();
        parameterGroupRepository.deleteAll();
        metricRepository.deleteAll();
        organizationRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected void registerTestUsers() {
        var admin = RegisterRequest.builder()
                .firstname("Admin")
                .lastname("Admin")
                .email("admin@mail.com")
                .password("password")
                .role(Role.ADMIN)
                .build();
        authenticationService.register(admin);

        var manager = RegisterRequest.builder()
                .firstname("Admin")
                .lastname("Admin")
                .email("manager@mail.com")
                .password("password")
                .role(Role.MANAGER)
                .build();
        authenticationService.register(manager);
    }

    @Transactional
    protected List<Organization> createOrganization() {
        var users = userRepository.findAll();
        Assertions.assertFalse(users.isEmpty());

        var organization = Organization.builder()
                .name("Test org")
                .users(users)
                .build();
        var org1 = organizationRepository.save(organization);

        accessToken=createAccessToken(org1);

        createMetrics(org1);

        var organization1 = Organization.builder()
                .name("Test org 1")
                .users(List.of(users.get(0)))
                .build();
        var org2 = organizationRepository.save(organization1);

        createMetrics(org2);

        return List.of(org1, org2);
    }

    @Transactional
    private void createMetrics(Organization organization) {

        var metrics = Instancio.ofList(Metric.class)
                .size(10)
                .generate(field(Metric::getName), gen -> gen.text().pattern("#c#c#c#c#c#c.#c#c#c#c#c#c#c#c#c#c#c.#c#c#c#c#c#c#c#c#c#c#c"))
                .set(field(Metric::getOrganization), organization)
                .ignore(field(Metric::getId))
                .ignore(field(Metric::getParameterGroups))
                .create();
        metricRepository.saveAll(metrics);
        metrics.forEach(this::createParameterGroups);
    }

    @Transactional
    private void createParameterGroups(Metric metric) {
        var parameterGroups = Instancio.ofList(ParameterGroup.class)
                .size(15)
                .ignore(field(ParameterGroup::getId))
                .set(field(ParameterGroup::getMetric), metric)
                .generate(field(ParameterGroup::getParameters), gen -> (Generator<String>) random -> {
                    Map<String, String> res = new HashMap<>();
                    for (int i = 0; i < random.intRange(0, 10); i++) {
                        res.put(random.lowerCaseAlphabetic(10), random.lowerCaseAlphabetic(10));
                    }
                    try {
                        return new ObjectMapper().writeValueAsString(res);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .create();
        parameterGroupRepository.saveAll(parameterGroups);
    }

    @Transactional
    private AccessToken createAccessToken(Organization organization) {
        return accessTokenRepository.save(AccessToken.builder()
                .organization(organization)
                .build());
    }
}
