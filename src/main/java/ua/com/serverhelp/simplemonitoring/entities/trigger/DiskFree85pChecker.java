package ua.com.serverhelp.simplemonitoring.entities.trigger;

import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.CheckTriggerException;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;
import ua.com.serverhelp.simplemonitoring.utils.MetricUnreachableException;

import java.util.List;
import java.util.Optional;

public class DiskFree85pChecker implements Checker{
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
        if(checkerArguments.size()!=2) throw new CheckTriggerException("DiskFree85pChecker::checkState parameter list not have correct count");
        Double freeBytes=null,usedBytes=null,sizeBytes=null;
        for (CheckerArgument checkerArgument:checkerArguments) {
            if (checkerArgument.getPosition()==1) {
                Optional<Event> event = storage.getFirstEventByParameterGroup(checkerArgument.getParameterGroup());
                if (event.isEmpty())
                    throw new MetricUnreachableException("DiskFree85pChecker::checkState list not have events");
                freeBytes = event.get().getValue();
            } else {
                Optional<Event> event = storage.getFirstEventByParameterGroup(checkerArgument.getParameterGroup());
                if (event.isEmpty())
                    throw new MetricUnreachableException("DiskFree85pChecker::checkState list not have events");
                sizeBytes = event.get().getValue();
            }
        }
        if(sizeBytes!=null && freeBytes!=null) {
            MYLog.printDebug1("DiskFree85pChecker freeBytes="+freeBytes+"  sizeBytes="+sizeBytes+"  freeBytes / sizeBytes="+freeBytes / sizeBytes+" res="+(freeBytes / sizeBytes>0.15));
            return freeBytes / sizeBytes>0.15;
        }
        throw new CheckTriggerException("DiskFree85pChecker::checkState value is negative");
    }

    @Override
    public String getName() {
        return "Disk free 85%";
    }

    @Override
    public String getDescription() {
        return "Disk user more than 85%";
    }
}
