package ua.com.serverhelp.simplemonitoring.entities.trigger;

import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.IParameterGroup;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.CheckTriggerException;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;
import ua.com.serverhelp.simplemonitoring.utils.MetricUnreachableException;

import java.time.Instant;
import java.util.List;

public class Last15minValuesChecker  implements Checker{
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
        if(checkerArguments.size()!=1) throw new CheckTriggerException("Last15minValuesChecker::checkState parameter list not have correct count");
        IParameterGroup iParameterGroup=checkerArguments.get(0).getParameterGroup();
        List<Event> events=storage.getEventsByParameterGroup(iParameterGroup, Instant.now().minusSeconds(900),Instant.now());
        MYLog.printDebug1("Last15minValuesChecker size="+events.size()+"  res="+!events.isEmpty());
        return !events.isEmpty();
    }

    @Override
    public String getName() {
        return "Last 15 min values";
    }

    @Override
    public String getDescription() {
        return "Not have last 15 min values";
    }
}
