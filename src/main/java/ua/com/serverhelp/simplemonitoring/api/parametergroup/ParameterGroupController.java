package ua.com.serverhelp.simplemonitoring.api.parametergroup;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.repository.UserRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ParameterGroupController {
    private final UserRepository userRepository;
    private final EntityManager entityManager;

//    @SchemaMapping(typeName = "ParameterGroup",field = "dataItems")
//    public List<DataItem> dataItems(ParameterGroup parameterGroup, Authentication authentication) throws Exception {
//        var userDetails = (UserDetails) authentication.getPrincipal();
//        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
//        TypedQuery<ParameterGroup> query = entityManager.createNamedQuery("User.findMetricsByUserAndMetric",ParameterGroup.class);
//        query.setParameter("user", user);
//        query.setParameter("parameterGroup", parameterGroup);
//        var parameterGroups=query.getResultList();
//        if(parameterGroups.size()==0){
//            throw new Exception();
//        }
//
//        return List.of();
//    }

    @SchemaMapping(typeName = "ParameterGroup", field = "dataItems")
    public List<DataItem> dataItems(ParameterGroup parameterGroup) {
        return List.of();
    }
}
