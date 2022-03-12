package ua.com.serverhelp.simplemonitoring.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueElement {
    private String path;
    private String json;
    private String options;
    private Instant timestamp;
    private Double value;
}
