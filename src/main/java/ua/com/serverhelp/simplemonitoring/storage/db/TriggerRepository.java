package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entities.trigger.Trigger;

import java.util.Optional;

public interface TriggerRepository extends JpaRepository<Trigger,Long> {
    @Cacheable(value = "TriggerRepository1",key = "{ #host, #checkerClass }")
    Optional<Trigger> findByHostAndCheckerClass(String host,String checkerClass);
    @CacheEvict(value = "TriggerRepository1",key = "{ #s.host, #s.checkerClass }")
    Trigger save(Trigger s);
}
