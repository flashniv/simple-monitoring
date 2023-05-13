package ua.com.serverhelp.simplemonitoring.entity.metric;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Metric {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Organization organization;

}
