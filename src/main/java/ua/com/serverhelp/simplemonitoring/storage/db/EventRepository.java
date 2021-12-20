package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event,Long>,EventRepositoryCustom {
    @Transactional
    @Modifying
    @Query("delete from Event e where e.timestamp < ?1")
    void deleteByTimestampMoreThan(Instant start);
    List<Event> findByParameterGroup(ParameterGroup parameterGroup, Sort unsorted);
    List<Event> findByParameterGroupAndTimestampBetween(ParameterGroup parameterGroup, Instant startDate, Instant endDate, Sort timestamp);
    Optional<Event> findFirstByParameterGroupOrderByTimestampDesc(ParameterGroup parameterGroup);
}
