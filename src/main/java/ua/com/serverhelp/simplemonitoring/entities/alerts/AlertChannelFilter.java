package ua.com.serverhelp.simplemonitoring.entities.alerts;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class AlertChannelFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private AlertChannel alertChannel;
    private String name;
    private String expression;

    public boolean matchFilter(Alert alert){
        return alert.getTrigger().getHost().matches(expression);
    }
}
