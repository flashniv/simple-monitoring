package ua.com.serverhelp.simplemonitoring.entity.triggers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.Expression;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ExpressionException;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;

@Entity
@Data
public class Trigger {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

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

    public Boolean checkTrigger() throws JsonProcessingException, ClassNotFoundException, NoSuchMethodException, ExpressionException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var objectMapper = new ObjectMapper();
        var confNode = objectMapper.readTree(conf);

        String className = confNode.get("class").asText();
        Class<?> classType = Class.forName(className);

        Expression<Boolean> expression = (Expression<Boolean>) classType.getConstructor().newInstance();
        expression.initialize(objectMapper.writeValueAsString(confNode.get("parameters")));

        return expression.getValue();
    }
}
