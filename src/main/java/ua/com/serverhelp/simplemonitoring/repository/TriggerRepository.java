package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;

import java.util.Optional;

public interface TriggerRepository extends JpaRepository<Trigger,Long> {
    Optional<Trigger> findByOrganizationAndTriggerId(Organization organization, String triggerId);
}
