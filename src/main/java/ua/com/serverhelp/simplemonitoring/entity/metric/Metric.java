package ua.com.serverhelp.simplemonitoring.entity.metric;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Metric {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Organization organization;

    @OneToMany
    @JoinColumn(name = "metric_id")
    private List<ParameterGroup> parameterGroups;
}
