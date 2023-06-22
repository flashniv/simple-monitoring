package ua.com.serverhelp.simplemonitoring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.user.User;

import java.util.List;
import java.util.Optional;

public interface MetricRepository extends JpaRepository<Metric, Long>, MetricCustomRepository {

    @Query("select m from Metric m join Organization o join o.users u where m.organization=o and u = :user")
    List<Metric> findAllByUser(User user);

    Page<Metric> findAllByOrganization(Organization organization, Pageable pageable);

    Optional<Metric> findByIdAndOrganization(Long id, Organization org);
}
