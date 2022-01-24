package ua.com.serverhelp.simplemonitoring.entities.trigger;

import lombok.extern.slf4j.Slf4j;
import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.IParameterGroup;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.CheckTriggerException;
import ua.com.serverhelp.simplemonitoring.utils.MetricUnreachableException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
public class Last24hValuesChecker implements Checker{
    /**
     * Check specific trigger condition
     *
     * @param events List of List Events to use as parameter for condition
     * @return true if all parameters are within acceptable limits
     *         or false if we have problem
     * @throws CheckTriggerException if data is not compat for this trigger
     * @throws MetricUnreachableException if list of event have zero size
     */
    @Override
    public boolean checkState(List<? extends CheckerArgument> checkerArguments, Storage storage) throws CheckTriggerException, MetricUnreachableException {
        if(checkerArguments.size()!=1) throw new CheckTriggerException("Last24hValuesChecker::checkState parameter list not have correct count");
        IParameterGroup iParameterGroup=checkerArguments.get(0).getParameterGroup();
        List<Event> events=storage.getEventsByParameterGroup(iParameterGroup, Instant.now().minus(25, ChronoUnit.HOURS),Instant.now());
        log.debug(checkerArguments.get(0).getParameterGroup().getMetric()+checkerArguments.get(0).getParameterGroup().getJson()+" Last24hValuesChecker size="+events.size()+"  res="+!events.isEmpty());
        return !events.isEmpty();
    }

    @Override
    public String getName() {
        return "Last 24h values";
    }

    @Override
    public String getDescription() {
        return "Not have last 24h values";
    }
}
