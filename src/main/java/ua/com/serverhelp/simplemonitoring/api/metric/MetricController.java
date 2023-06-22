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
import ua.com.serverhelp.simplemonitoring.repository.OrganizationRepository;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.repository.UserRepository;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MetricController {
    private final UserRepository userRepository;
    private final MetricRepository metricRepository;
    private final ParameterGroupRepository parameterGroupRepository;
    private final OrganizationRepository organizationRepository;

    @QueryMapping
    public Page<Metric> metrics(@Argument UUID orgId, @Argument Integer page, @Argument Integer size, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(orgId, user).orElseThrow();

        return metricRepository.findAllByOrganization(org, PageRequest.of(page, size));
    }

    @QueryMapping
    public Metric metric(@Argument Long metricId, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var metric = metricRepository.findById(metricId).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(metric.getOrganization().getId(), user).orElseThrow();

        return metric;
    }

    @SchemaMapping(typeName = "Metric", field = "parameterGroups")
    public Page<ParameterGroup> parameterGroups(@Argument Integer page, @Argument Integer size, Metric metric) {
        return parameterGroupRepository.findAllByMetric(metric, PageRequest.of(page, size));
    }
}
