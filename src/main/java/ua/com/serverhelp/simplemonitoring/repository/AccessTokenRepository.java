package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entity.accesstoken.AccessToken;

import java.util.UUID;

public interface AccessTokenRepository extends JpaRepository<AccessToken, UUID>,AccessTokenCustomRepository {
}
