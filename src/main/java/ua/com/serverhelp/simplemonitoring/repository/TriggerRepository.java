package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;

import java.util.List;
import java.util.Optional;

public interface TriggerRepository extends JpaRepository<Trigger, Long> {
    Optional<Trigger> findByOrganizationAndTriggerId(Organization organization, String triggerId);

    Page<Trigger> findAllByOrganization(Organization org, Pageable pageable);

    List<Trigger> findAllByOrganization(Organization org);
}
