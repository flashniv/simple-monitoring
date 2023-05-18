package ua.com.serverhelp.simplemonitoring.entity.parametergroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataItem {
    private Organization organization;
    private String path;
    private String parameters;
    private Instant timestamp;
    private Double value;
}
