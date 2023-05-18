package ua.com.serverhelp.simplemonitoring.entity.parametergroup;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Entity
public class ParameterGroup {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Metric metric;

    @Column(columnDefinition = "TEXT")
    private String parameters = "{}";

    @Transient
    private List<DataItem> dataItems;
}
