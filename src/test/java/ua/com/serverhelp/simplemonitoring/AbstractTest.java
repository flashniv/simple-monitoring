package ua.com.serverhelp.simplemonitoring;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.serverhelp.simplemonitoring.api.auth.type.RegisterRequest;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.user.Role;
import ua.com.serverhelp.simplemonitoring.repository.MetricRepository;
import ua.com.serverhelp.simplemonitoring.repository.OrganizationRepository;
import ua.com.serverhelp.simplemonitoring.repository.TokenRepository;
import ua.com.serverhelp.simplemonitoring.repository.UserRepository;
import ua.com.serverhelp.simplemonitoring.service.AuthenticationService;

import java.util.List;

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
    protected MetricRepository metricRepository;


    @AfterEach
    void tearDown() {
        tokenRepository.deleteAll();
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
    private void createMetrics(Organization organization){
        var metric= Metric.builder()
                .name("test.metric1")
                .organization(organization)
                .build();
        metricRepository.save(metric);
        var metric1= Metric.builder()
                .name("test.metric2")
                .organization(organization)
                .build();
        metricRepository.save(metric1);
    }
}
