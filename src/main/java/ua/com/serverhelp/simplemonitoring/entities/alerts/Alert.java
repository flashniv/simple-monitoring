package ua.com.serverhelp.simplemonitoring.entities.alerts;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ua.com.serverhelp.simplemonitoring.entities.trigger.Trigger;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @Getter
    @Setter
    private Long id;
    @ManyToOne (optional=false)
    @JoinColumn (name="parameter_group_trigger_id")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Getter
    @Setter
    private Trigger trigger;
    @Getter
    @Setter
    private Instant startDate;
    @Getter
    @Setter
    private Instant stopDate;
    @Getter
    @Setter
    private String operationData;
}