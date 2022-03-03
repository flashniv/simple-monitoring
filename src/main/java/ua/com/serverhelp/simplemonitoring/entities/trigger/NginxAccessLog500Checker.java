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
public class NginxAccessLog500Checker  implements Checker{
    @Override
    public boolean checkState(List<? extends CheckerArgument> checkerArguments, Storage storage) throws CheckTriggerException, MetricUnreachableException {
        if(checkerArguments.size()!=1) throw new CheckTriggerException("NginxAccessLog500Checker::checkState parameter list not have correct count");
        IParameterGroup iParameterGroup=checkerArguments.get(0).getParameterGroup();
        List<Event> events=storage.getEventsByParameterGroup(iParameterGroup, Instant.now().minus(5, ChronoUnit.MINUTES),Instant.now());
        log.debug(checkerArguments.get(0).getParameterGroup().getMetric() + checkerArguments.get(0).getParameterGroup().getJson() + " NginxAccessLog500Checker size=" + events.size() + "  res=" + events.isEmpty());
        return events.isEmpty();
    }

    @Override
    public String getName() {
        return "Detected 500 errors";
    }

    @Override
    public String getDescription() {
        return "Detected 500 error codes in access log";
    }
}
