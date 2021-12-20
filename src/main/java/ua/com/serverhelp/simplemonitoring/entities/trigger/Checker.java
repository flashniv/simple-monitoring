package ua.com.serverhelp.simplemonitoring.entities.trigger;

import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.CheckTriggerException;
import ua.com.serverhelp.simplemonitoring.utils.MetricUnreachableException;

import java.util.List;

public interface Checker {
    /**
     * Check specific trigger condition
     *
     * @param events List of List Events to use as parameter for condition
     * @return true if all parameters are within acceptable limits
     *         or false if we have problem
     * @throws CheckTriggerException if data is not compat for this trigger
     * @throws MetricUnreachableException if list of event have zero size
     */
    boolean checkState(List<? extends CheckerArgument> parameterGroups, Storage storage) throws CheckTriggerException, MetricUnreachableException;
    String getName();
    String getDescription();
}
