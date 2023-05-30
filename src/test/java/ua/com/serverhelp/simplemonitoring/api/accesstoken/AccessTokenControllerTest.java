package ua.com.serverhelp.simplemonitoring.api.accesstoken;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;

import java.util.List;

@AutoConfigureGraphQlTester
class AccessTokenControllerTest extends AbstractTest {
    @Autowired
    private GraphQlTester tester;

    @BeforeEach
    void setUp() {
        registerTestUsers();
        createOrganization();
    }

    @Test
    @WithMockUser("admin@mail.com")
    void createOrganizationAccessToken() {
        List<Organization> organizations = organizationRepository.findAll();
        Assertions.assertFalse(organizations.isEmpty());
        var org = organizations.get(0);

        var document = """
                mutation {
                    createOrganizationAccessToken(orgId:"__ID__")
                }
                """.replace("__ID__", org.getId().toString());
        var token = tester
                .document(document)
                .execute()
                .path("createOrganizationAccessToken")
                .entity(String.class)
                .get();
        Assertions.assertTrue(token.contains("-"));
    }
}