package ua.com.serverhelp.simplemonitoring.entities.alerts;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class AlertFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String expression;

    public boolean matchFilter(Alert alert){
        return alert.getTrigger().getHost().matches(expression);
    }
}
