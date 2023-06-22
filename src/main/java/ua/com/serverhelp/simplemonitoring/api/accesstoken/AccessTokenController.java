package ua.com.serverhelp.simplemonitoring.api.accesstoken;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import ua.com.serverhelp.simplemonitoring.entity.accesstoken.AccessToken;
import ua.com.serverhelp.simplemonitoring.repository.AccessTokenRepository;
import ua.com.serverhelp.simplemonitoring.repository.OrganizationRepository;
import ua.com.serverhelp.simplemonitoring.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AccessTokenController {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final AccessTokenRepository accessTokenRepository;

    @MutationMapping
    public String createOrganizationAccessToken(@Argument UUID orgId, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(orgId, user).orElseThrow();
        var accessToken = AccessToken.builder()
                .organization(org)
                .build();
        var persistAccessToken = accessTokenRepository.save(accessToken);
        return persistAccessToken.getId().toString();
    }

    @QueryMapping
    public List<AccessToken> organizationAccessTokens(@Argument UUID orgId, Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        var org = organizationRepository.findByIdAndUsers(orgId, user).orElseThrow();
        return accessTokenRepository.findAllByOrganization(org);
    }
}
