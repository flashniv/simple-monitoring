package ua.com.serverhelp.simplemonitoring.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;

public class ParameterGroupCustomRepositoryImpl implements ParameterGroupCustomRepository {
    @Autowired
    private MetricRepository metricRepository;
    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public ParameterGroup getOrCreateParameterGroup(Organization organization, String path, String parameters) {
        var metric = metricRepository.getOrCreateMetric(organization, path);
        var parameterGroupsQuery = entityManager.createQuery("select pg from ParameterGroup pg where pg.metric=:metric and pg.parameters=:params", ParameterGroup.class);
        parameterGroupsQuery.setParameter("metric", metric);
        parameterGroupsQuery.setParameter("params", parameters);
        var parameterGroups = parameterGroupsQuery.getResultList();

        if (!parameterGroups.isEmpty()) {
            return parameterGroups.get(0);
        }
        var parameterGroup = ParameterGroup.builder()
                .metric(metric)
                .parameters(parameters)
                .build();
        entityManager.persist(parameterGroup);

        return parameterGroup;
    }
}
