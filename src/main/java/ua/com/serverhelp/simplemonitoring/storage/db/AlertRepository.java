package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;
import ua.com.serverhelp.simplemonitoring.entities.trigger.Trigger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert,Long> {
    Optional<Alert> getFirstAlertByTriggerAndStopDateIsNull(Trigger trigger);
    List<Alert> findByStartDateBetween(Instant begin, Instant end,Sort sort);
}
