package ua.com.serverhelp.simplemonitoring.entities.trigger;

import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.IParameterGroup;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.CheckTriggerException;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;
import ua.com.serverhelp.simplemonitoring.utils.MetricUnreachableException;

import java.util.List;
import java.util.Optional;

public class LoadAvgChecker implements Checker{
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
    public boolean checkState(List<? extends CheckerArgument> checkerArguments, Storage storage) throws CheckTriggerException, MetricUnreachableException{
        if(checkerArguments.size()!=1) throw new CheckTriggerException("LoadAvgChecker::checkState parameter list not have correct count");
        IParameterGroup iParameterGroup= checkerArguments.get(0).getParameterGroup();
        Optional<Event> eventList=storage.getFirstEventByParameterGroup(iParameterGroup);
        if(eventList.isEmpty()) throw new MetricUnreachableException("LoadAvgChecker::checkState list not have events");
        Event event=eventList.get();
        if (event.getValue()>5.0){
            MYLog.printDebug1("LoadAvgChecker val="+event.getValue()+"  res=false");
            return false;
        }else if (event.getValue()>=0.0){
            MYLog.printDebug1("LoadAvgChecker val="+event.getValue()+"  res=true");
            return true;
        }
        throw new CheckTriggerException("LoadAvgChecker::checkState value is negative");
    }

    @Override
    public String getName() {
        return "Load Average";
    }

    @Override
    public String getDescription() {
        return "Processor load too high";
    }
}
