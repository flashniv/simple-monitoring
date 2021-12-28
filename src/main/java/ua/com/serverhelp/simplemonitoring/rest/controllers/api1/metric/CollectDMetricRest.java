package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/apiv1/metric/collectd")
public class CollectDMetricRest {
    @Autowired
    private MetricsQueue metricsQueue;
    @Autowired
    private Storage storage;

    /*
{
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
    @PostMapping("/")
    public ResponseEntity<String> receiveData(@RequestHeader("X-Project") String proj,@RequestBody String data){
        JSONArray jsonArray=new JSONArray(data);
        String host="";
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject value = jsonArray.getJSONObject(i);
            Instant timestamp=Instant.now(); //Instant.ofEpochSecond ((long) (value.getDouble("time"))); //TODO release diff
            host = value.getString("host");
            String plugin = value.getString("plugin");

            String pluginInstance = value.getString("plugin_instance");
            String type = value.getString("type");
            String typeInstance = value.getString("type_instance");
            JSONArray dsnames = value.getJSONArray("dsnames");
            JSONArray dstypes = value.getJSONArray("dstypes");
            JSONArray values = value.getJSONArray("values");
            String path = "collectd." + proj + "." + host + "." + plugin;

            for (int j = 0; j < dsnames.length(); j++) {
                JSONObject parameters=new JSONObject();
                if(!pluginInstance.isEmpty()){
                    parameters.put("instance", pluginInstance);
                }
                if(!type.isEmpty()){
                    parameters.put("type", type);
                }
                if(!typeInstance.isEmpty()){
                    parameters.put("type_instance", typeInstance);
                }
                parameters.put("ds_name", dsnames.getString(j));
                parameters.put("ds_type", dstypes.getString(j));
                Double dobValue= values.getDouble(j);
                metricsQueue.putData(path,parameters.toString(),getOptionsByMetric(plugin),timestamp,dobValue);
            }
        }

        //create trigger for LA
        Metric load=storage.getOrCreateMetric("collectd." + proj + "." + host+".load");
        storage.createIfNotExistTrigger("collectd." + proj + "." + host+".load","ua.com.serverhelp.simplemonitoring.entities.trigger.LoadAvgChecker",storage.getOrCreateParameterGroup(load,"{\"ds_name\":\"longterm\",\"ds_type\":\"gauge\",\"type\":\"load\"}"));
        storage.createIfNotExistTrigger("collectd." + proj + "." + host+".load","ua.com.serverhelp.simplemonitoring.entities.trigger.Last15minValuesChecker",storage.getOrCreateParameterGroup(load,"{\"ds_name\":\"longterm\",\"ds_type\":\"gauge\",\"type\":\"load\"}"));
        //create DF trigger
        Metric dfMetric= storage.getOrCreateMetric("collectd." + proj + "." + host+".df");
        List<ParameterGroup> dfParameterGroupList=storage.getParameterGroups(dfMetric);
        List<String> mountPoints=new ArrayList<>();
        for (int i=0;i< dfParameterGroupList.size();i++) {
            ParameterGroup parameterGroup= dfParameterGroupList.get(i);
            if(!mountPoints.contains(parameterGroup.getParameters().get("instance"))){
                mountPoints.add(parameterGroup.getParameters().get("instance"));
            }
        }
        for (String mountPoint: mountPoints){
            storage.createIfNotExistTrigger(
                    "collectd." + proj + "." + host+".df."+mountPoint,
                    "ua.com.serverhelp.simplemonitoring.entities.trigger.DiskFreeU85pChecker",
                    storage.getOrCreateParameterGroup(dfMetric,"{\"ds_name\":\"value\",\"instance\":\""+mountPoint+"\",\"type_instance\":\"free\",\"ds_type\":\"gauge\",\"type\":\"df_complex\"}"),
                    storage.getOrCreateParameterGroup(dfMetric,"{\"ds_name\":\"value\",\"instance\":\""+mountPoint+"\",\"type_instance\":\"used\",\"ds_type\":\"gauge\",\"type\":\"df_complex\"}")
            );
        }
        //Swap usage checker
        Metric swap= storage.getOrCreateMetric("collectd." + proj + "." + host+".swap");
        storage.createIfNotExistTrigger(
                "collectd." + proj + "." + host+".swap",
                "ua.com.serverhelp.simplemonitoring.entities.trigger.SwapUUsageChecker",
                storage.getOrCreateParameterGroup(swap,"{\"ds_name\":\"value\",\"type_instance\":\"free\",\"ds_type\":\"gauge\",\"type\":\"swap\"}"),
                storage.getOrCreateParameterGroup(swap,"{\"ds_name\":\"value\",\"type_instance\":\"used\",\"ds_type\":\"gauge\",\"type\":\"swap\"}")
        );

        return ResponseEntity.ok().body("Success");
    }

    private String getOptionsByMetric(String plugin){
        JSONObject res=new JSONObject();
        switch (plugin){
            case "disk":
            case "interface":
            case "nginx":
                res.put("diff", true);
        }
        return res.toString();
    }
}
