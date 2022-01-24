package ua.com.serverhelp.simplemonitoring.entities.alerts;

import lombok.Data;
import ua.com.serverhelp.simplemonitoring.entities.account.User;

import javax.persistence.*;

@Entity
@Data
public class AlertChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private User owner;
    private String alerterClass;
    private String parameters;

    public void printAlert(Alert alert){

    }
}
