package ua.com.serverhelp.simplemonitoring.repository;

import ua.com.serverhelp.simplemonitoring.entity.accesstoken.AccessToken;

import java.util.Optional;
import java.util.UUID;

public interface AccessTokenCustomRepository {

    Optional<AccessToken> findByIdCached(UUID token);
}
