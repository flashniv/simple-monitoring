package ua.com.serverhelp.simplemonitoring.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.user.User;
import ua.com.serverhelp.simplemonitoring.repository.UserRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @QueryMapping
    public User user(Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow();
    }

    @SchemaMapping(typeName = "User", field = "role")
    public String getRole(Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return user.getRole().name();
    }

    @SchemaMapping(typeName = "User", field = "organizations")
    public List<Organization> getOrganizations(Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return user.getOrganizations();
    }
}
