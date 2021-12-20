package ua.com.serverhelp.simplemonitoring.queue;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class QueueParameter {
    private Instant timestamp;
    private Double value;
}
