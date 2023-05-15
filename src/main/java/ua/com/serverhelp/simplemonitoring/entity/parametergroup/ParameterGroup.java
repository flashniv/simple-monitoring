package ua.com.serverhelp.simplemonitoring.entity.parametergroup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    private String parameters = "{}";

    public Set<Map.Entry<String, String>> getParametersMap() {
        Map<String, String> map = new HashMap<>();
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(parameters);
            jsonNode.fields().forEachRemaining(stringJsonNodeEntry -> {
                map.put(stringJsonNodeEntry.getKey(), stringJsonNodeEntry.getValue().textValue());
            });
            System.out.println();
        } catch (Exception e) {
            log.error("JSON parse error", e);
        }
        return map.entrySet();
    }
}
