package ua.com.serverhelp.simplemonitoring.api.alert;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alerter;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;
import ua.com.serverhelp.simplemonitoring.repository.AlerterRepository;
import ua.com.serverhelp.simplemonitoring.repository.OrganizationRepository;
import ua.com.serverhelp.simplemonitoring.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AlerterController {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final AlerterRepository alerterRepository;

    @MutationMapping
    public ResponseEntity<Alerter> createAlerter(@Argument UUID orgId, @Argument String minPriority, @Argument String className, @Argument String properties, @Argument String description, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(orgId, user).orElseThrow();
        var alerter = Alerter.builder()
                .organization(org)
                .className(className)
                .minPriority(TriggerPriority.valueOf(minPriority))
                .properties(properties)
                .description(description)
                .build();
        return ResponseEntity.ok(alerterRepository.save(alerter));
    }

    @QueryMapping
    public ResponseEntity<List<Alerter>> alerters(@Argument UUID orgId, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(orgId, user).orElseThrow();

        return ResponseEntity.ok(alerterRepository.findAllByOrganization(org));
    }
}
