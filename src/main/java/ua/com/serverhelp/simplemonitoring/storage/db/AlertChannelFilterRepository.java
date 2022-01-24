package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertChannel;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertChannelFilter;

import java.util.List;

public interface AlertChannelFilterRepository extends JpaRepository<AlertChannelFilter,Long> {
    List<AlertChannelFilter> findAllByAlertChannel(AlertChannel alertChannel);
}
