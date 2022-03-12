package ua.com.serverhelp.simplemonitoring.queue;

import ua.com.serverhelp.simplemonitoring.entities.event.Event;

import java.time.Instant;
import java.util.List;

/*{
  "values":[0,0],
  "dstypes":["derive","derive"],
  "dsnames":["rx","tx"],
  "time":1630108891.967,
  "interval":90.000,
  "host":"s-kvm2",
  "plugin":"interface",
  "plugin_instance":"vnet1",
  "type":"if_dropped",
  "type_instance":""
}
{
  "values":[0.381944927576563],
  "dstypes":["gauge"],
  "dsnames":["value"],
  "time":1630108891.967,
  "interval":90.000,
  "host":"s-kvm2",
  "plugin":"cpu",
  "plugin_instance":"",
  "type":"percent",
  "type_instance":"wait"
}*/

public interface MetricsQueue {
    List<Event> getEvents();
    void putData(QueueElement queueElement);
}
