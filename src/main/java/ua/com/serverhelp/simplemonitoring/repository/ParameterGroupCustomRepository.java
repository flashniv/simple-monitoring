package ua.com.serverhelp.simplemonitoring.repository;

import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;

public interface ParameterGroupCustomRepository {
    ParameterGroup getOrCreateParameterGroup(Organization organization, String path, String parameters);
}
