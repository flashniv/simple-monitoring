package ua.com.serverhelp.simplemonitoring.queue;

import lombok.Data;

import java.time.Instant;

@Data
public class QueueElement {
    private String path;
    private String json;
    private String options;
    private Instant timestamp;
    private Double value;
}
