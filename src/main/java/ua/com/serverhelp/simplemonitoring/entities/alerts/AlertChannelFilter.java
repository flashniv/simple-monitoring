package ua.com.serverhelp.simplemonitoring.entities.alerts;

import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Data
public class AlertChannelFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne (optional=false)
    @JoinColumn (name="alert_channel_id")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private AlertChannel alertChannel;
    private String name;
    private String expression;

    public boolean matchFilter(Alert alert){
        return alert.getTrigger().getHost().matches(expression);
    }
}
