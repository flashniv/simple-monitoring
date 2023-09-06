package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;

import java.util.List;

public interface ParameterGroupRepository extends JpaRepository<ParameterGroup, Long>, ParameterGroupCustomRepository {
    Page<ParameterGroup> findAllByMetric(Metric metric, Pageable pageable);

    List<ParameterGroup> findAllByMetric(Metric metric);
}
