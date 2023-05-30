package ua.com.serverhelp.simplemonitoring.repository;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.serverhelp.simplemonitoring.entity.accesstoken.AccessToken;
import ua.com.serverhelp.simplemonitoring.service.cache.CacheService;

import java.util.Optional;
import java.util.UUID;

public class AccessTokenCustomRepositoryImpl implements AccessTokenCustomRepository {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<AccessToken> findByIdCached(UUID token) {
        var cachedToken = cacheService.<AccessToken>getItem("AccessTokenCustomRepositoryImpl::findByIdCached", token.toString());
        if (cachedToken != null) {
            return Optional.of(cachedToken);
        }
        var foundToken = entityManager.find(AccessToken.class, token);
        if (foundToken != null) {
            cacheService.setItem("AccessTokenCustomRepositoryImpl::findByIdCached", token.toString(), foundToken);
            return Optional.of(foundToken);
        }
        return Optional.empty();
    }
}
