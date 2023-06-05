package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    Page<Alert> findAllByOrganization(Organization org, Pageable pageable);

    Page<Alert> findAllByTrigger(Trigger trigger, Pageable pageable);

    void deleteAllByTrigger(Trigger trigger);
}
