package ua.com.serverhelp.simplemonitoring.entities.trigger;

import lombok.Getter;
import lombok.Setter;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;

import javax.persistence.*;

@Entity
public class ParameterGroupCheckerArgument implements CheckerArgument{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private int position;
    @Getter
    @Setter
    @ManyToOne (optional=false)
    @JoinColumn (name="parameter_group_id")
    private ParameterGroup parameterGroup;
    @Getter
    @Setter
    @ManyToOne (optional=false)
    @JoinColumn (name="trigger_id")
    private Trigger trigger;
}
