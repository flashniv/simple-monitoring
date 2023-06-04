package ua.com.serverhelp.simplemonitoring.api.alert;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alerter;
import ua.com.serverhelp.simplemonitoring.entity.alert.inputtype.IAlerter;
import ua.com.serverhelp.simplemonitoring.repository.AlerterRepository;
import ua.com.serverhelp.simplemonitoring.repository.OrganizationRepository;
import ua.com.serverhelp.simplemonitoring.repository.UserRepository;
import ua.com.serverhelp.simplemonitoring.rest.exceptions.AccessDeniedError;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AlerterController {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final AlerterRepository alerterRepository;

    @MutationMapping
    public Alerter createAlerter(@Argument IAlerter inputAlerter, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(inputAlerter.getOrganizationId(), user).orElseThrow();
        var alerter = Alerter.builder()
                .organization(org)
                .className(inputAlerter.getClassName())
                .minPriority(inputAlerter.getMinPriority())
                .properties(inputAlerter.getProperties())
                .description(inputAlerter.getDescription())
                .build();
        return alerterRepository.save(alerter);
    }

    @MutationMapping
    public Alerter updateAlerter(@Argument Long alerterId, @Argument IAlerter inputAlerter, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(inputAlerter.getOrganizationId(), user).orElseThrow();
        var alerter = alerterRepository.findById(alerterId).orElseThrow();
        if (alerter.getOrganization().getId().equals(org.getId())) {
            alerter.setClassName(inputAlerter.getClassName());
            alerter.setProperties(inputAlerter.getProperties());
            alerter.setDescription(inputAlerter.getDescription());
            alerter.setMinPriority(inputAlerter.getMinPriority());

            return alerterRepository.save(alerter);
        }
        throw new AccessDeniedError("Access denied");
    }

    @MutationMapping
    public String deleteAlerter(@Argument Long alerterId, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var alerter = alerterRepository.findById(alerterId).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(alerter.getOrganization().getId(), user).orElseThrow();

        alerterRepository.delete(alerter);
        return "Success";
    }

    @QueryMapping
    public List<Alerter> alerters(@Argument UUID orgId, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(orgId, user).orElseThrow();

        return alerterRepository.findAllByOrganization(org);
    }
}
