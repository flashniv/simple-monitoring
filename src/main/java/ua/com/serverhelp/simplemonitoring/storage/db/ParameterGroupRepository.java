package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;

import java.util.List;
import java.util.Optional;

public interface ParameterGroupRepository extends JpaRepository<ParameterGroup,Long> {
    @Cacheable(value = "ParameterGroup", key = "{ #metric.getPath(), #json }",unless = "#result == null")
    Optional<ParameterGroup> findByMetricAndJson(Metric metric,String json);

    //@CacheEvict(value = "ParameterGroup",key = "{ #s.metric, #s.json }")
    //ParameterGroup save(ParameterGroup s);

    List<ParameterGroup> findByMetric(Metric metric);
}
