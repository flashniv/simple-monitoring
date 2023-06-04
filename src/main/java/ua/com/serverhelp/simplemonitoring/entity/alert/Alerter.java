package ua.com.serverhelp.simplemonitoring.entity.alert;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alerter {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Organization organization;
    @Enumerated(EnumType.STRING)
    private TriggerPriority minPriority;
    private String description;
    private String className;
    private String properties;
}
