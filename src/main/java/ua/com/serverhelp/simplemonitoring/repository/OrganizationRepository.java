package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    @EntityGraph("Organization.users")
    List<Organization> findAllByUsers(User user);

    @EntityGraph("Organization.users")
    Optional<Organization> findByIdAndUsers(UUID id, User user);

    @Query("select o from Organization o where o.id=:id")
    @EntityGraph("Organization.metrics")
    Optional<Organization> findByIdWithMetrics(UUID id);
}
