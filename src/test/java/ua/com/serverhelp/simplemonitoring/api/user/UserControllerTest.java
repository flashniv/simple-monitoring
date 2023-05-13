package ua.com.serverhelp.simplemonitoring.api.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.user.User;

@Slf4j
@AutoConfigureGraphQlTester
class UserControllerTest extends AbstractTest {
    @Autowired
    private GraphQlTester tester;

    @Test
    @WithMockUser("admin@mail.com")
    void user() {
        registerTestUsers();

        var document = """
                {
                    user {
                        id
                        firstname
                        lastname
                        email
                        role
                        organizations {
                            id
                            name
                        }
                    }
                }
                """;

        var user = tester
                .document(document)
                .execute()
                .path("user")
                .entity(User.class)
                .get();
        Assertions.assertEquals("Admin", user.getFirstname());
    }

    @Test
    @WithMockUser("admin@mail.com")
    void getRole() {
        registerTestUsers();

        var document = """
                {
                    user {
                        id
                        firstname
                        lastname
                        email
                        role
                        organizations {
                            id
                            name
                        }
                    }
                }
                """;

        var user = tester
                .document(document)
                .execute()
                .path("user")
                .entity(User.class)
                .get();
        Assertions.assertEquals("ADMIN", user.getRole().name());
    }

    @Test
    @WithMockUser("admin@mail.com")
    void getOrganizations() {
        registerTestUsers();
        createOrganization();

        var document = """
                {
                    user {
                        id
                        firstname
                        lastname
                        email
                        role
                        organizations {
                            id
                            name
                        }
                    }
                }
                """;

        var user = tester
                .document(document)
                .execute()
                .path("user")
                .entity(User.class)
                .get();
        Assertions.assertFalse(user.getOrganizations().isEmpty());
        var organization = user.getOrganizations().get(0);
        Assertions.assertEquals("Test org", organization.getName());
    }
}