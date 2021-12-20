package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertFilter;

public interface AlertFilterRepository extends JpaRepository<AlertFilter,Long> {
}
