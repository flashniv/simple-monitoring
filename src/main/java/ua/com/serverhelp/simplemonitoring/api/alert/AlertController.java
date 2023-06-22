package ua.com.serverhelp.simplemonitoring.api.alert;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;
import ua.com.serverhelp.simplemonitoring.repository.AlertRepository;
import ua.com.serverhelp.simplemonitoring.repository.OrganizationRepository;
import ua.com.serverhelp.simplemonitoring.repository.UserRepository;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AlertController {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final AlertRepository alertRepository;

    @QueryMapping
    public Page<Alert> alerts(@Argument UUID orgId, @Argument Integer page, @Argument Integer size, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(orgId, user).orElseThrow();

        return alertRepository.findAllByOrganization(org, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "alertTimestamp")));
    }
}
