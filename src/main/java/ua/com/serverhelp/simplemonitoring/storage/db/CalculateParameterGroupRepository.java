package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.CalculateParameterGroup;

import java.util.List;
import java.util.Optional;

public interface CalculateParameterGroupRepository extends JpaRepository<CalculateParameterGroup,Long> {
    @Cacheable(value = "CalculateParameterGroup",key = "{ #metric.path, #json }")
    Optional<CalculateParameterGroup> findByMetricAndJson(Metric metric, String json);
    @CacheEvict(value = "CalculateParameterGroup",key = "{ #s.metric.path, #s.json }")
    CalculateParameterGroup save(CalculateParameterGroup s);
    List<CalculateParameterGroup> findByMetric(Metric metric);
}
