package ua.com.serverhelp.simplemonitoring.entity.triggers;

import lombok.Getter;

public enum TriggerPriority {
    DEBUG(1),
    NOT_CLASSIFIED(2),
    INFO(3),
    AVERAGE(4),
    WARNING(5),
    HIGH(6),
    DISASTER(7);
    @Getter
    private final int value;

    TriggerPriority(int value) {
        this.value = value;
    }
}
