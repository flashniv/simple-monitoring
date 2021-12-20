package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entities.trigger.ParameterGroupCheckerArgument;
import ua.com.serverhelp.simplemonitoring.entities.trigger.Trigger;

import java.util.List;

public interface ParameterGroupCheckerArgumentRepository extends JpaRepository<ParameterGroupCheckerArgument,Long> {
    List<ParameterGroupCheckerArgument> findByTrigger(Trigger trigger);
}
