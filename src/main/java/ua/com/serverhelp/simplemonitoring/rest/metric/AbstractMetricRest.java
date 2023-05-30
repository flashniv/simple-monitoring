package ua.com.serverhelp.simplemonitoring.rest.metric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.repository.AccessTokenRepository;
import ua.com.serverhelp.simplemonitoring.repository.MetricRepository;
import ua.com.serverhelp.simplemonitoring.repository.OrganizationRepository;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.rest.exceptions.AccessDeniedError;
import ua.com.serverhelp.simplemonitoring.service.DataItemsService;
import ua.com.serverhelp.simplemonitoring.service.TriggerService;
import ua.com.serverhelp.simplemonitoring.service.cache.CacheService;

import java.util.UUID;

public abstract class AbstractMetricRest {
    @Value("${metric-storage.metrics-directory}")
    protected String dirName;

    @Autowired
    protected OrganizationRepository organizationRepository;
    @Autowired
    protected MetricRepository metricRepository;
    @Autowired
    protected ParameterGroupRepository parameterGroupRepository;
    @Autowired
    protected AccessTokenRepository accessTokenRepository;
    @Autowired
    protected DataItemsService dataItemsService;
    @Autowired
    protected TriggerService triggerService;
    @Autowired
    protected CacheService cacheService;

    protected Organization getOrganization(UUID token) {
        var accessToken = accessTokenRepository.findByIdCached(token).orElseThrow(() -> new AccessDeniedError("Token not valid"));
        return accessToken.getOrganization();
    }

}
