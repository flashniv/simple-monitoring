package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;

import java.util.List;
import java.util.Optional;

public interface ParameterGroupRepository extends JpaRepository<ParameterGroup,Long> {
    List<ParameterGroup> findAllByMetric(Metric metric);

    Optional<ParameterGroup> findByMetricAndParameters(Metric metric, String parameters);
}
