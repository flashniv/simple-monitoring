package ua.com.serverhelp.simplemonitoring.entities.event;

import lombok.Getter;
import lombok.Setter;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(indexes = {
        @Index(name = "event_timestamp_pg_id",columnList = ("parameter_group_id,\"timestamp\""))
})
public class Event {
    @Id
    //@GeneratedValue(generator = "event-id-generator")
    //@GenericGenerator(name = "event-id-generator", strategy = "ua.com.serverhelp.simplemonitoring.utils.EventIdGenerator")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;
    @ManyToOne
    @JoinColumn (name="parameter_group_id")
    @Getter
    @Setter
    private ParameterGroup parameterGroup;
    @Getter
    @Setter
    private Instant timestamp;
    @Getter
    @Setter
    private Double value;

}
