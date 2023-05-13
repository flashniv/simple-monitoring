package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;

public interface MetricRepository extends JpaRepository<Metric,Long> {

}
