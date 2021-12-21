package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertFilter;

import java.util.List;

public interface AlertFilterRepository extends JpaRepository<AlertFilter,Long> {
    @Cacheable(value = "AlertFilterRepository")
    List<AlertFilter> findAll();
    @CacheEvict(value = "AlertFilterRepository")
    AlertFilter save(AlertFilter alertFilter);
}
