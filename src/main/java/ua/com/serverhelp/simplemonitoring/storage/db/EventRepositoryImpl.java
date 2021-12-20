package ua.com.serverhelp.simplemonitoring.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.CalculateParameterGroup;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventRepositoryImpl implements EventRepositoryCustom{
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Event> findByParameterGroupAndTimestampBetweenWithPlaceholder(ParameterGroup parameterGroup, Instant startDate, Instant endDate) {
        List<Event> events=new ArrayList<>();
        int period=getPeriod(startDate, endDate);
        Query query=entityManager.createNativeQuery("SELECT generate_series as timestamp,avg(value) as value,json " +
                "FROM generate_series( ?1\\:\\:timestamp, ?2, '"+period+" minutes'\\:\\:interval) left join" +
                "   (select * from event e join parameter_group pg on e.parameter_group_id=pg.id" +
                "   where pg.metric_id='"+parameterGroup.getMetric().getPath()+"' and pg.json like '"+parameterGroup.getJson()+"'" +
                ") as events on events.timestamp between generate_series-'"+period+" minutes'\\:\\:interval and generate_series " +
                "group by generate_series,json order by timestamp");
        query.setParameter(1, Date.from(startDate), TemporalType.TIMESTAMP);
        query.setParameter(2, Date.from(endDate), TemporalType.TIMESTAMP);
        List<Object[]> list=query.getResultList();
        for(Object[] objects:list){
            Event event=new Event();
            Timestamp timestamp=(Timestamp) objects[0];
            Double val=(Double) objects[1];

            if (val==null){ //TODO create null
                val=0.0;
            }

            event.setValue(val);
            event.setTimestamp(timestamp.toInstant());
            events.add(event);
        }
        return events;
    }

    @Override
    public List<Event> findByCalculateParameterGroupAndTimestampBetween(CalculateParameterGroup parameterGroup, Instant begin, Instant end, Sort sort) {
        List<Event> events=new ArrayList<>();
        int period=getPeriod(begin, end);
        Query query=entityManager.createNativeQuery("SELECT timestamp,"+parameterGroup.getFunctionName()+"(value) " +
                "from ( " +
                "SELECT generate_series as timestamp,avg(value) as value,json " +
                "FROM generate_series( ?1\\:\\:timestamp, ?2, '"+period+" minute'\\:\\:interval) left join" +
                "   (select * from event e join parameter_group pg on e.parameter_group_id=pg.id" +
                "   where pg.metric_id='"+parameterGroup.getMetric().getPath()+"' and pg.json like '"+parameterGroup.getJson()+"'" +
                ") as events on events.timestamp between generate_series-'"+period+" minute'\\:\\:interval and generate_series " +
                "group by generate_series,json) as events " +
                "group by timestamp " +
                "order by timestamp;");
        query.setParameter(1, Date.from(begin), TemporalType.TIMESTAMP);
        query.setParameter(2, Date.from(end), TemporalType.TIMESTAMP);
        List<Object[]> list=query.getResultList();
        for(Object[] objects:list){
            Event event=new Event();
            Timestamp timestamp=(Timestamp) objects[0];
            Double val=(Double) objects[1];

            if (val==null){ //TODO create null
                val=0.0;
            }

            event.setValue(val);
            event.setTimestamp(timestamp.toInstant());
            events.add(event);
        }
        return events;
    }

    private int getPeriod(Instant begin,Instant end){
        Duration duration=Duration.between(begin, end);
        if(duration.getSeconds()>7400 && duration.getSeconds()<=21600){
            return 5;
        }else if(duration.getSeconds()>=21600 && duration.getSeconds()<43200){
            return 10;
        }else if(duration.getSeconds()>=43200){
            return 30;
        }
        return 1;
    }

}
