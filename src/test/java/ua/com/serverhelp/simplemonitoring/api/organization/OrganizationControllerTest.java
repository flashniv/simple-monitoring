package ua.com.serverhelp.simplemonitoring.api.organization;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;

@Slf4j
@AutoConfigureGraphQlTester
class OrganizationControllerTest extends AbstractTest {
    @Autowired
    private GraphQlTester tester;

    @BeforeEach
    void setUp() {
        registerTestUsers();
    }

    @Test
    @WithMockUser("admin@mail.com")
    void testCreateOrganization() {
        var document = """
                mutation {
                    createOrganization(name:"new org"){
                        id
                        name
                        users{
                            id
                        }
                    }
                }
                """;
        var organization = tester
                .document(document)
                .execute()
                .path("createOrganization")
                .entity(Organization.class)
                .get();
        Assertions.assertEquals("new org", organization.getName());
        Assertions.assertEquals(1, organization.getUsers().size());
    }

    @Test
    @WithMockUser("admin@mail.com")
    void organizations() {
        createOrganization();
        var document = """
                {
                    organizations{
                        id
                        name
                    }
                }
                """;
        var organizations = tester
                .document(document)
                .execute()
                .path("organizations[*]")
                .entityList(Organization.class)
                .get();
        Assertions.assertEquals(2, organizations.size());
        var organization = organizations.get(0);
        Assertions.assertEquals("Test org", organization.getName());
    }

    @Test
    @WithMockUser("admin@mail.com")
    void organization() {
        var orgs = createOrganization();

        var document = """
                {
                    organization(id:"__ID__"){
                        id
                        name
                    }
                }
                """.replace("__ID__", orgs.get(0).getId().toString());
        var organization = tester
                .document(document)
                .execute()
                .path("organization")
                .entity(Organization.class)
                .get();
        Assertions.assertEquals("Test org", organization.getName());
    }

    @Test
    @WithMockUser("admin@mail.com")
    void users() {
        var orgs = createOrganization();

        var document = """
                {
                    organization(id:"__ID__"){
                        id
                        name
                        users{
                            id
                        }
                    }
                }
                """.replace("__ID__", orgs.get(1).getId().toString());
        var organization = tester
                .document(document)
                .execute()
                .path("organization")
                .entity(Organization.class)
                .get();
        Assertions.assertEquals(1, organization.getUsers().size());

        var document1 = """
                {
                    organization(id:"__ID__"){
                        id
                        name
                        users{
                            id
                        }
                    }
                }
                """.replace("__ID__", orgs.get(0).getId().toString());
        var organization1 = tester
                .document(document1)
                .execute()
                .path("organization")
                .entity(Organization.class)
                .get();
        Assertions.assertEquals(2, organization1.getUsers().size());
    }

    @Test
    @WithMockUser("admin@mail.com")
    void metrics() {
        var orgs = createOrganization();

        var document = """
                {
                    organization(id:"__ID__"){
                        id
                        name
                        users{
                            id
                        }
                        metrics{
                            id
                        }
                    }
                }
                """.replace("__ID__", orgs.get(0).getId().toString());
        var organization = tester
                .document(document)
                .execute()
                .path("organization")
                .entity(Organization.class)
                .get();
        Assertions.assertEquals("Test org", organization.getName());
        Assertions.assertEquals(2, organization.getUsers().size());
        Assertions.assertEquals(10, organization.getMetrics().size());
    }
}