package ua.com.serverhelp.simplemonitoring.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.api.auth.type.AuthenticationRequest;
import ua.com.serverhelp.simplemonitoring.api.auth.type.RegisterRequest;
import ua.com.serverhelp.simplemonitoring.entity.user.Role;

@Slf4j
class AuthenticationServiceTest extends AbstractTest {
    @Test
    void register() {
        var admin = RegisterRequest.builder()
                .firstname("Admin")
                .lastname("Admin")
                .email("admin@mail.com")
                .password("password")
                .role(Role.ADMIN)
                .build();
        Assertions.assertDoesNotThrow(() -> authenticationService.register(admin));
        var user = userRepository.findByEmail("admin@mail.com");
        Assertions.assertTrue(user.isPresent());
    }

    @Test
    void authenticateBadCredentials() {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("admin@mail.com")
                .password("password")
                .build();
        Assertions.assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(authenticationRequest));
    }

    @Test
    void authenticate() {
        registerTestUsers();
        var authenticationRequest = AuthenticationRequest.builder()
                .email("admin@mail.com")
                .password("password")
                .build();
        var authenticationResponse = authenticationService.authenticate(authenticationRequest);

        log.info("Authenticated access token" + authenticationResponse.getAccessToken());
        log.info("Authenticated refresh token" + authenticationResponse.getRefreshToken());

        Assertions.assertFalse(authenticationResponse.getAccessToken().isBlank());
        Assertions.assertFalse(authenticationResponse.getRefreshToken().isBlank());
    }

    @Test
    void refreshToken() throws InterruptedException {
        registerTestUsers();
        var auth = authenticationService.authenticate(AuthenticationRequest.builder()
                .email("admin@mail.com")
                .password("password")
                .build());
        Thread.sleep(1000);
        var newAuth = authenticationService.refreshToken("Bearer " + auth.getRefreshToken());
        Assertions.assertFalse(newAuth.getAccessToken().isBlank());
        Assertions.assertFalse(newAuth.getRefreshToken().isBlank());
    }
}