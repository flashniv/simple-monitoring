package ua.com.serverhelp.simplemonitoring.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import ua.com.serverhelp.simplemonitoring.entities.account.Role;
import ua.com.serverhelp.simplemonitoring.entities.account.User;
import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertChannel;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertChannelFilter;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertFilter;
import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.CalculateParameterGroup;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.IParameterGroup;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.entities.trigger.CheckerArgument;
import ua.com.serverhelp.simplemonitoring.entities.trigger.ParameterGroupCheckerArgument;
import ua.com.serverhelp.simplemonitoring.entities.trigger.Trigger;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric.exporter.BlackBoxMetricRest;
import ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric.exporter.NginxAccessLogMetricRest;
import ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric.exporter.NodeMetricRest;
import ua.com.serverhelp.simplemonitoring.storage.db.*;
import ua.com.serverhelp.simplemonitoring.utils.HealthMetrics;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DataBaseStorage implements Storage {
    @Autowired
    private MetricsQueue metricsQueue;
    @Autowired
    private AlertFilterRepository alertFilterRepository;
    @Autowired
    private HealthMetrics healthMetrics;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MetricRepository metricRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ParameterGroupRepository parameterGroupRepository;
    @Autowired
    private CalculateParameterGroupRepository calculateParameterGroupRepository;
    @Autowired
    private TriggerRepository triggerRepository;
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private ParameterGroupCheckerArgumentRepository parameterGroupCheckerArgumentRepository;
    @Autowired
    private AlertChannelFilterRepository alertChannelFilterRepository;
    @Autowired
    private AlertChannelRepository alertChannelRepository;
    @Autowired
    private NginxAccessLogMetricRest nginxAccessLogMetricRest;
    @Autowired
    private BlackBoxMetricRest blackBoxMetricRest;
    @Autowired
    private NodeMetricRest nodeMetricRest;

    @Override
    public void commitHistory() {
        nginxAccessLogMetricRest.processItems();
        blackBoxMetricRest.processItems();
        nodeMetricRest.processItems();
        eventRepository.saveAll(metricsQueue.getEvents());
    }

    @Override
    public void commitTriggers() {
        nginxAccessLogMetricRest.processTriggers();
        blackBoxMetricRest.processTriggers();
        nodeMetricRest.processTriggers();
    }

    @Override
    public List<IParameterGroup> getAllTypeParameterGroups(Metric metric) {
        List<ParameterGroup> realParameterGroups=parameterGroupRepository.findByMetric(metric);
        List<CalculateParameterGroup> calculateParameterGroupList=calculateParameterGroupRepository.findByMetric(metric);
        List<IParameterGroup> res = new ArrayList<>(calculateParameterGroupList);
        res.addAll(realParameterGroups);

        return res;
    }
    @Override
    public List<ParameterGroup> getParameterGroups(Metric metric) {
        return parameterGroupRepository.findByMetric(metric);
    }

    @Override
    public void createIfNotExistCalculateParameterGroup(String path, String json,String functionName) {
        Metric metric=getOrCreateMetric(path);
        Optional<CalculateParameterGroup> optionalCalculateParameterGroup=calculateParameterGroupRepository.findByMetricAndJson(metric, json);
        if (optionalCalculateParameterGroup.isEmpty()) {
            CalculateParameterGroup calculateParameterGroup = new CalculateParameterGroup();
            calculateParameterGroup.setMetric(metric);
            calculateParameterGroup.setJson(json);
            calculateParameterGroup.setFunctionName(functionName);

            calculateParameterGroupRepository.save(calculateParameterGroup);
        }
    }

    @Override
    public Metric getMetric(String path) {
        Optional<Metric> res=metricRepository.findById(path);
        return res.orElse(null);
    }

    @Override
    public List<Metric> getAllMetrics(){
        return metricRepository.findAll(Sort.by("path"));
    }

    @Override
    public Metric getOrCreateMetric(String path) {
        Optional<Metric> res=metricRepository.findById(path);
        if (res.isPresent()){
            return res.get();
        }
        Metric metric=new Metric();
        metric.setPath(path);
        saveMetric(metric);
        return metric;
    }

    @Override
    public void saveMetric(Metric metric) {
        metricRepository.save(metric);
    }

    @Override
    public void saveParameterGroup(ParameterGroup parameterGroup) {
        parameterGroupRepository.save(parameterGroup);
    }

    @Override
    public ParameterGroup getOrCreateParameterGroup(Metric metric, String json) {
        Optional<ParameterGroup> res=parameterGroupRepository.findByMetricAndJson(metric, json);
        ParameterGroup parameterGroup;
        if (res.isPresent()){
            parameterGroup=res.get();
        }else {
            parameterGroup = new ParameterGroup();
            parameterGroup.setMetric(metric);
            parameterGroup.setJson(json);
            saveParameterGroup(parameterGroup);
        }
        log.info("getOrCreateParameterGroup "+parameterGroup);
        return parameterGroup;
    }

    @Override
    public List<Event> getEventsByParameterGroup(IParameterGroup iparameterGroup, Instant begin, Instant end,boolean withPlaceholder) {
        List<Event> events=new ArrayList<>();
        if(iparameterGroup instanceof ParameterGroup) {
            if(withPlaceholder){
                events = eventRepository.findByParameterGroupAndTimestampBetweenWithPlaceholder((ParameterGroup) iparameterGroup, begin, end);
            }else {
                events = eventRepository.findByParameterGroupAndTimestampBetween((ParameterGroup) iparameterGroup, begin, end, Sort.by(Sort.Direction.ASC, "timestamp"));
            }
        } else if(iparameterGroup instanceof CalculateParameterGroup){
            events = eventRepository.findByCalculateParameterGroupAndTimestampBetween((CalculateParameterGroup) iparameterGroup, begin, end, Sort.by(Sort.Direction.ASC, "timestamp"));
        }
        return events;
    }
    @Override
    public List<Event> getEventsByParameterGroup(IParameterGroup iparameterGroup, Instant begin, Instant end) {
        return getEventsByParameterGroup(iparameterGroup, begin, end, false);
    }

    @Override
    public void clearHistory(){
        eventRepository.deleteByTimestampMoreThan(Instant.now().minus(2,ChronoUnit.DAYS));
    }
    @Override
    public boolean deleteMetric(String id) {
        Optional<Metric> metric=metricRepository.findById(id);
        if (metric.isPresent()){
            List<ParameterGroup> parameterGroups=parameterGroupRepository.findByMetric(metric.get());
            for (ParameterGroup parameterGroup:parameterGroups){
                List<Event> events=eventRepository.findByParameterGroup(parameterGroup,Sort.unsorted());
                //List<Alert> alerts=alertRepository.findByMetric(metric.get()); //TODO release it
                //alertRepository.deleteAll(alerts);
                eventRepository.deleteAll(events);
            }
            parameterGroupRepository.deleteAll(parameterGroups);
            metricRepository.delete(metric.get());
            return true;
        }
        return false;
    }

    @Override
    public List<AlertFilter> getAllAlertFilters() {
        return alertFilterRepository.findAll();
    }

    @Override
    public void saveAlertFilter(AlertFilter alertFilter) {
        alertFilterRepository.save(alertFilter);
    }

    @Override
    public void deleteAlertFilterById(String id) {
        alertFilterRepository.deleteById(Long.parseLong(id));
    }

    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);
        roleRepository.flush();
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void createIfNotExistTrigger(String host, String checkerClass,ParameterGroup... parameterGroups) {
        log.info("createIfNotExistTrigger with ParameterGroups "+ Arrays.toString(parameterGroups));
        Optional<Trigger> trigger=triggerRepository.findByHostAndCheckerClass(host, checkerClass);
        if (trigger.isEmpty()){
            Trigger newTrigger=new Trigger();
            newTrigger.setCheckerClass(checkerClass);
            newTrigger.setHost(host);
            triggerRepository.save(newTrigger);

            List<ParameterGroupCheckerArgument> parameterGroupCheckerArguments=new ArrayList<>();
            int i=1;
            for (ParameterGroup parameterGroup:parameterGroups){
                ParameterGroupCheckerArgument parameterGroupCheckerArgument=new ParameterGroupCheckerArgument();
                parameterGroupCheckerArgument.setPosition(i++);
                parameterGroupCheckerArgument.setParameterGroup(parameterGroup);
                parameterGroupCheckerArgument.setTrigger(newTrigger);
                parameterGroupCheckerArguments.add(parameterGroupCheckerArgument);
            }
            log.info("createIfNotExistTrigger parameterGroupCheckerArguments "+parameterGroupCheckerArguments);
            parameterGroupCheckerArgumentRepository.saveAll(parameterGroupCheckerArguments);
        }
    }

    @Override
    public void createIfNotExistTrigger(String metric, String json, String checkerClass) {
        log.info("createIfNotExistTrigger with JSON "+json);
        Metric metric1=getOrCreateMetric(metric);
        ParameterGroup parameterGroup=getOrCreateParameterGroup(metric1, json);
        Optional<Trigger> parameterGroupTrigger=triggerRepository.findByHostAndCheckerClass(metric, checkerClass);
        log.info("createIfNotExistTrigger JSON parameterGroupTrigger "+parameterGroupTrigger.isPresent());
        if (parameterGroupTrigger.isEmpty()){
            Trigger newTrigger=new Trigger();
            newTrigger.setCheckerClass(checkerClass);
            newTrigger.setHost(metric);
            triggerRepository.save(newTrigger);

            ParameterGroupCheckerArgument checkerArgument=new ParameterGroupCheckerArgument();
            checkerArgument.setPosition(1);
            checkerArgument.setParameterGroup(parameterGroup);
            checkerArgument.setTrigger(newTrigger);
            log.info("createIfNotExistTrigger JSON checkerArgument "+checkerArgument);
            parameterGroupCheckerArgumentRepository.save(checkerArgument);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Trigger> getAllTriggers() {
        return triggerRepository.findAll(); //TODO add calculate parameter trigger
    }

    @Override
    public Optional<Alert> getFirstAlertByTriggerAndStopDateIsNull(Trigger trigger) {
        return alertRepository.getFirstAlertByTriggerAndStopDateIsNull(trigger);
    }

    @Override
    public List<Alert> getAlerts(Instant begin, Instant end, Sort sort) {
        return alertRepository.findByStartDateBetween(begin,end,sort);
    }

    @Override
    public void saveAlert(Alert alert) {
            alertRepository.save(alert);
    }

    @Override
    public List<? extends CheckerArgument> getCheckerArgumentsByTrigger(Trigger trigger) {
        return parameterGroupCheckerArgumentRepository.findByTrigger(trigger);
    }

    @Override
    public Optional<Event> getFirstEventByParameterGroup(IParameterGroup parameterGroup) {
        Optional<Event> optionalEvent=Optional.empty();
        if(parameterGroup instanceof ParameterGroup){
            optionalEvent=eventRepository.findFirstByParameterGroupOrderByTimestampDesc((ParameterGroup) parameterGroup);
        }else {
            //TODO release calc
        }
        return optionalEvent;
    }

    @Override
    public List<Alert> getAllAlerts(Sort startDate) {
        return alertRepository.findAll(startDate);
    }

    @Override
    public List<AlertChannelFilter> getAlertChannelFilters(AlertChannel alertChannel) {
        return alertChannelFilterRepository.findAllByAlertChannel(alertChannel);
    }

    @Override
    public List<AlertChannel> getAllAlertChannels() {
        return alertChannelRepository.findAll();
    }

}
