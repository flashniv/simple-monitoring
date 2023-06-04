package ua.com.serverhelp.simplemonitoring.entity.alert.inputtype;

import lombok.Data;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;

import java.util.UUID;

@Data
public class IAlerter {
    private UUID organizationId;
    private TriggerPriority minPriority;
    private String description;
    private String className;
    private String properties;
}
