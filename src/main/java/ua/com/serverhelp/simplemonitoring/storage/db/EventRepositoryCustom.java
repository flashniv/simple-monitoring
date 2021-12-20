package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.data.domain.Sort;
import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.CalculateParameterGroup;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;

import java.time.Instant;
import java.util.List;

public interface EventRepositoryCustom {
    List<Event> findByParameterGroupAndTimestampBetweenWithPlaceholder(ParameterGroup parameterGroup, Instant startDate, Instant endDate);
    List<Event> findByCalculateParameterGroupAndTimestampBetween(CalculateParameterGroup parameterGroup, Instant begin, Instant end, Sort timestamp);
}
