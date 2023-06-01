package ua.com.serverhelp.simplemonitoring.api.metric;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.repository.MetricRepository;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.repository.UserRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MetricController {
    private final UserRepository userRepository;
    private final MetricRepository metricRepository;
    private final ParameterGroupRepository parameterGroupRepository;

    @QueryMapping
    public List<Metric> metrics(Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        return metricRepository.findAllByUser(user);
    }

    @SchemaMapping(typeName = "Metric", field = "parameterGroups")
    public Page<ParameterGroup> parameterGroups(@Argument Integer page, @Argument Integer size, Metric metric) {
        return parameterGroupRepository.findAllByMetric(metric, PageRequest.of(page, size));
    }
}
