package ua.com.serverhelp.simplemonitoring.storage;

import org.springframework.data.domain.Sort;
import ua.com.serverhelp.simplemonitoring.entities.account.Role;
import ua.com.serverhelp.simplemonitoring.entities.account.User;
import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertChannel;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertChannelFilter;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertFilter;
import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.IParameterGroup;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.entities.trigger.CheckerArgument;
import ua.com.serverhelp.simplemonitoring.entities.trigger.Trigger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface Storage {
    List<Metric> getAllMetrics();
    Metric getMetric(String path);
    boolean deleteMetric(String id);
    Metric getOrCreateMetric(String path);
    void saveMetric(Metric metric);

    void saveParameterGroup(ParameterGroup parameterGroup);
    ParameterGroup getOrCreateParameterGroup(Metric metric, String json);
    List<ParameterGroup> getParameterGroups(Metric metric);
    List<IParameterGroup> getAllTypeParameterGroups(Metric metric);
    void createIfNotExistCalculateParameterGroup(String path, String json,String functionName);

    List<Event> getEventsByParameterGroup(IParameterGroup parameterGroup, Instant begin, Instant end);
    List<Event> getEventsByParameterGroup(IParameterGroup parameterGroup, Instant begin, Instant end,boolean withPlaceholder);

    void clearHistory();
    void commitHistory();
    void commitTriggers();

    List<AlertFilter> getAllAlertFilters();
    void saveAlertFilter(AlertFilter alertFilter);
    void deleteAlertFilterById(String id);

    //security
    void saveRole(Role role);
    void saveUser(User user);

    void createIfNotExistTrigger(String host, String checkerClass,ParameterGroup... parameterGroups);
    void createIfNotExistTrigger(String metric,String json,String checkerClass);

    List<Trigger> getAllTriggers();

    Optional<Alert> getFirstAlertByTriggerAndStopDateIsNull(Trigger trigger);

    List<Alert> getAlerts(Instant begin, Instant end, Sort sort);
    void saveAlert(Alert alert);

    List<? extends CheckerArgument> getCheckerArgumentsByTrigger(Trigger trigger);

    Optional<Event> getFirstEventByParameterGroup(IParameterGroup parameterGroup);

    List<Alert> getAllAlerts(Sort startDate);

    List<AlertChannelFilter> getAlertChannelFilters(AlertChannel alertChannel);

    List<AlertChannel> getAllAlertChannels();
}
