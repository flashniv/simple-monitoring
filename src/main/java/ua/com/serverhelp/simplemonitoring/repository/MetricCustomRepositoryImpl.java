package ua.com.serverhelp.simplemonitoring.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.service.cache.CacheService;

public class MetricCustomRepositoryImpl implements MetricCustomRepository {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public Metric getOrCreateMetric(Organization organization, String path) {
        var cachedMetric = cacheService.<Metric>getItem("MetricCustomRepositoryImpl::getOrCreateMetric", organization.getId() + "." + path);
        if (cachedMetric != null) {
            return cachedMetric;
        }

        var typedQuery = entityManager.createQuery("select m from Metric m where m.organization=:org and m.name=:path", Metric.class);
        typedQuery.setParameter("org", organization);
        typedQuery.setParameter("path", path);
        var metrics = typedQuery.getResultList();
        Metric metric;

        if (metrics.isEmpty()) {
            metric = Metric.builder()
                    .name(path)
                    .organization(organization)
                    .build();
            entityManager.persist(metric);
        } else {
            metric = metrics.get(0);
        }

        cacheService.setItem("MetricCustomRepositoryImpl::getOrCreateMetric", organization.getId() + "." + path, metric);

        return metric;
    }

    /*@Override
    public Metric saveWithClearCache(Metric metric) {
        entityManager.persist(metric);

        cacheService.setItem("Metrics", metric.getOrganization().getId() + "." + metric.getName(), metric);

        return metric;
    }*/


}
