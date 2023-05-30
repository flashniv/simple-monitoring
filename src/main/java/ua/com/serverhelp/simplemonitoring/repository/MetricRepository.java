package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.user.User;

import java.util.List;

public interface MetricRepository extends JpaRepository<Metric, Long>, MetricCustomRepository {

    @Query("select m from Metric m join Organization o join o.users u where m.organization=o and u = :user")
    List<Metric> findAllByUser(User user);
}
