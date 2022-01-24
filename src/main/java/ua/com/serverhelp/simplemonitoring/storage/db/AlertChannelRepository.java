package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertChannel;

public interface AlertChannelRepository extends JpaRepository<AlertChannel,Long> {
}
