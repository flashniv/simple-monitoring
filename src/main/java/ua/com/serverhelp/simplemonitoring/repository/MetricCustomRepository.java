package ua.com.serverhelp.simplemonitoring.repository;

import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;

public interface MetricCustomRepository {
    Metric getOrCreateMetric(Organization organization, String path);
}
