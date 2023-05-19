package ua.com.serverhelp.simplemonitoring.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;

public class MetricCustomRepositoryImpl implements MetricCustomRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public Metric getOrCreateMetric(Organization organization, String path) {
        var typedQuery = entityManager.createQuery("select m from Metric m where m.organization=:org and m.name=:path", Metric.class);
        typedQuery.setParameter("org", organization);
        typedQuery.setParameter("path", path);
        var metrics = typedQuery.getResultList();

        if (!metrics.isEmpty()) {
            return metrics.get(0);
        }
        var metric = Metric.builder()
                .name(path)
                .organization(organization)
                .build();
        entityManager.persist(metric);

        return metric;

    }
}
