package ua.com.serverhelp.simplemonitoring.entities.metric;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@ToString
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Metric {
    @Id
    @Setter
    @Getter
    private String path;
}
