package ua.com.serverhelp.simplemonitoring.api.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.user.User;
import ua.com.serverhelp.simplemonitoring.repository.MetricRepository;
import ua.com.serverhelp.simplemonitoring.repository.OrganizationRepository;
import ua.com.serverhelp.simplemonitoring.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class OrganizationController {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final MetricRepository metricRepository;

    @MutationMapping
    public Organization createOrganization(@Argument String name, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return organizationRepository.save(
                Organization.builder()
                        .name(name)
                        .users(List.of(user))
                        .build()
        );
    }

    @QueryMapping
    public List<Organization> organizations(Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        return organizationRepository.findAllByUsers(user);
    }

    @QueryMapping
    public Organization organization(@Argument UUID id, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        return organizationRepository.findByIdAndUsers(id, user).orElseThrow();
    }

    @SchemaMapping(typeName = "Organization", field = "users")
    public List<User> users(Organization organization) {
        return organization.getUsers();
    }

    @SchemaMapping(typeName = "Organization", field = "metrics")
    public Page<Metric> metrics(@Argument Integer page, @Argument Integer size, Organization organization) {
//        var persistentOrg = organizationRepository.findByIdWithMetrics(organization.getId()).orElseThrow();
//        return persistentOrg.getMetrics();
        return metricRepository.findAllByOrganization(organization, PageRequest.of(page, size));
    }
}
