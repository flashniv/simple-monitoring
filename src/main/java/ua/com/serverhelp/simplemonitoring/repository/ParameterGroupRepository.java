package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;

import java.util.List;

public interface ParameterGroupRepository extends JpaRepository<ParameterGroup, Long>, ParameterGroupCustomRepository {
    List<ParameterGroup> findAllByMetric(Metric metric);
}
