package ua.com.serverhelp.simplemonitoring.api.parametergroup;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;

import java.util.List;

@Slf4j
@AutoConfigureGraphQlTester
class ParameterGroupControllerTest extends AbstractTest {
    @Autowired
    private GraphQlTester tester;

    @BeforeEach
    void setUp() {
        registerTestUsers();
        createOrganization();
    }

    @Test
    @WithMockUser("admin@mail.com")
    void parameters() {
        var document = """
                {
                    metrics{
                        id
                        name
                        parameterGroups{
                            id
                            parameters{
                                name
                                parameterValue
                            }

                        }
                    }
                }
                """;
        var metrics = tester
                .document(document)
                .execute();
        log.debug("json:" + metrics.toString());
//                .path("metrics")
//                .entityList(Metric.class)
//                .get();
//        Assertions.assertEquals(20, metrics.size());

    }

    @Test
    void parameterGroup() {
        List<ParameterGroup> parameterGroups=parameterGroupRepository.findAll();

        var document = """
                    {
                        parameterGroup(parameterGroupID:"__ID__"){
                            id
                            parameters
                        }
                    }
                    """.replace("__ID__", parameterGroups.get(0).getId().toString());
        var metrics = tester
                .document(document)
                .execute()
                .path("parameterGroup")
                .entity(ParameterGroup.class)
                .get();
    }
}
