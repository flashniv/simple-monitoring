package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;

public interface ParameterGroupRepository extends JpaRepository<ParameterGroup,Long> {
}
