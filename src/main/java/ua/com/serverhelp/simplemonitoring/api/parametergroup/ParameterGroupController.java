package ua.com.serverhelp.simplemonitoring.api.parametergroup;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.repository.UserRepository;

@Controller
@RequiredArgsConstructor
public class ParameterGroupController {
    private final UserRepository userRepository;
    private final ParameterGroupRepository parameterGroupRepository;

//    @SchemaMapping(typeName = "ParameterGroup", field = "parameters")
//    public Set<Map.Entry<String, String>> parameters(ParameterGroup parameterGroup) {
//        return parameterGroup.getParametersMap();
//    }

    @QueryMapping
    public ParameterGroup parameterGroup(@Argument Long parameterGroupID) {
        var parameterGroup = parameterGroupRepository.findById(parameterGroupID).orElseThrow();
        return parameterGroup;
    }
}
