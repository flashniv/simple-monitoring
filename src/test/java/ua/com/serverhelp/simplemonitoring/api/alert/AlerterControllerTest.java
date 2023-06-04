package ua.com.serverhelp.simplemonitoring.api.alert;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alerter;

@Slf4j
@AutoConfigureGraphQlTester
class AlerterControllerTest extends AbstractTest {
    @Autowired
    private GraphQlTester tester;

    @Test
    @WithMockUser("admin@mail.com")
    void createAlerter() {
        registerTestUsers();
        var organizations = createOrganization();
        Assertions.assertFalse(organizations.isEmpty());
        var organization = organizations.get(0);

        var document = """
                mutation {
                    createAlerter(inputAlerter:{
                            organizationId:"__orgId__"
                            minPriority:HIGH
                            description:"desc1"
                            className:"ua.com.serverhelp.simplemonitoring.service.alert.alerters.DummyAlertSender"
                            properties:"{}"
                        
                    }){
                        id
                        className
                        properties
                        minPriority
                        description
                        organization{
                            id
                        }
                    }
                }
                """.replace("__orgId__", organization.getId().toString());
        var alerter = tester
                .document(document)
                .execute()
                .path("createAlerter")
                .entity(Alerter.class)
                .get();
        Assertions.assertEquals("desc1", alerter.getDescription());
    }

    @Test
    @WithMockUser("admin@mail.com")
    void updateAlerter() {
        registerTestUsers();
        var organizations = createOrganization();
        Assertions.assertFalse(organizations.isEmpty());
        var organization = organizations.get(0);

        var document = """
                mutation {
                    updateAlerter(alerterId:1,inputAlerter:{
                            organizationId:"__orgId__"
                            minPriority:HIGH
                            description:"desc1"
                            className:"ua.com.serverhelp.simplemonitoring.service.alert.alerters.DummyAlertSender"
                            properties:"{}"
                        
                    }){
                        id
                        className
                        properties
                        minPriority
                        description
                        organization{
                            id
                        }
                    }
                }
                """.replace("__orgId__", organization.getId().toString());
        var alerter = tester
                .document(document)
                .execute()
                .path("updateAlerter")
                .entity(Alerter.class)
                .get();
        Assertions.assertEquals("desc1", alerter.getDescription());
    }

    @Test
    @WithMockUser("admin@mail.com")
    void deleteAlerter() {
        registerTestUsers();
        createOrganization();

        var document = """
                mutation {
                    deleteAlerter(alerterId:1)
                }
                """;
        var result = tester
                .document(document)
                .execute()
                .path("deleteAlerter")
                .entity(String.class)
                .get();
        Assertions.assertEquals("Success", result);
    }

    @Test
    @WithMockUser("admin@mail.com")
    void alerters() {
        registerTestUsers();
        var organizations = createOrganization();
        Assertions.assertFalse(organizations.isEmpty());
        var organization = organizations.get(0);

        var document = """
                {
                    alerters(orgId:"__orgId__"){
                        id
                        className
                        properties
                        minPriority
                        description
                        organization{
                            id
                        }
                    }
                }
                """.replace("__orgId__", organization.getId().toString());
        var alerters = tester
                .document(document)
                .execute()
                .path("alerters")
                .entityList(Alerter.class)
                .get();
        Assertions.assertEquals(1, alerters.size());
    }
}