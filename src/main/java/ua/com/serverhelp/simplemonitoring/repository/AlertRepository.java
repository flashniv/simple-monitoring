package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
