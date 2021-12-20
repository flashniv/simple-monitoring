package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;

public interface MetricRepository extends JpaRepository<Metric,String> {
}
