package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alerter;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;

import java.util.List;

public interface AlerterRepository extends JpaRepository<Alerter, Long> {
    List<Alerter> findAllByOrganization(Organization organization);
}
