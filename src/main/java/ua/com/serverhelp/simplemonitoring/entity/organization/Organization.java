package ua.com.serverhelp.simplemonitoring.entity.organization;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(name = "Organization.users", attributeNodes = {@NamedAttributeNode("users")}),
        @NamedEntityGraph(name = "Organization.metrics", attributeNodes = {@NamedAttributeNode("metrics")})
})
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "organization_user",
            joinColumns = {@JoinColumn(name = "organization_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    @Column(nullable = false)
    private List<User> users = new ArrayList<>();
    @OneToMany
    @JoinColumn(name = "organization_id")
    private List<Metric> metrics = new ArrayList<>();
}
