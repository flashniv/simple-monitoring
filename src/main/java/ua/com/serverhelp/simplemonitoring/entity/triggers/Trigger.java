package ua.com.serverhelp.simplemonitoring.entity.triggers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.Expression;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ExpressionException;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trigger {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String triggerId;

    @Column(columnDefinition = "TEXT")
    private String description = "";

    @ManyToOne
    @JoinColumn(nullable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    private TriggerStatus lastStatus = TriggerStatus.UNCHECKED;
    @Enumerated(EnumType.STRING)
    private TriggerPriority priority = TriggerPriority.NOT_CLASSIFIED;

    private Instant lastStatusUpdate = Instant.now();

    private boolean enabled = true;
    private int suppressedScore = 0;
    private boolean muted = false;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conf;

    @Transient
    private List<Alert> alerts=new ArrayList<>();

    public Boolean checkTrigger() throws JsonProcessingException, ClassNotFoundException, NoSuchMethodException, ExpressionException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var objectMapper = new ObjectMapper();
        var confNode = objectMapper.readTree(conf);

        String className = confNode.get("class").asText();
        Class<?> classType = Class.forName(className);

        Expression<Boolean> expression = (Expression<Boolean>) classType.getConstructor().newInstance();
        expression.initialize(objectMapper.writeValueAsString(confNode.get("parameters")).replaceAll("__organizationID__", organization.getId().toString()));

        return expression.getValue();
    }
}
