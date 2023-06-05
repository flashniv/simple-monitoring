package ua.com.serverhelp.simplemonitoring.entity.triggers.inputtype;

import lombok.Data;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;

import java.util.UUID;

@Data
public class ITrigger {
    private UUID organizationId;
    private String name;
    private String description;
    private String triggerId;
    private TriggerPriority priority;
    private Boolean enabled;
    private Integer suppressedScore;
    private Boolean muted;
    private String conf;
}
