package ua.com.serverhelp.simplemonitoring.entity.parametergroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataItem {
    private Instant timestamp;
    private Double value;
}
