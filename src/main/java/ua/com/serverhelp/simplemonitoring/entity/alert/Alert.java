package ua.com.serverhelp.simplemonitoring.entity.alert;

import jakarta.persistence.*;
import lombok.*;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerStatus;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Alert {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(optional = false)
    private Trigger trigger;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Organization organization;
    private Instant alertTimestamp = Instant.now();
    private String operationData;
    @Enumerated(EnumType.STRING)
    private TriggerStatus triggerStatus;
}