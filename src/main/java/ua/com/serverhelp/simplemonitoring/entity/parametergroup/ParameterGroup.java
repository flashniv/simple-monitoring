package ua.com.serverhelp.simplemonitoring.entity.parametergroup;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ParameterGroup {
    @Id
    @GeneratedValue
    private Long id;

    @ElementCollection
    @CollectionTable(name = "group_parameter_mapping",
            joinColumns = {@JoinColumn(name = "group_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "parameter_name")
    private Map<String,String> parameters=new HashMap<>();

    @ManyToOne
    @JoinColumn(nullable = false)
    private Metric metric;

}
