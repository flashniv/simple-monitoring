package ua.com.serverhelp.simplemonitoring.entities.parametergroup;

import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;

import java.util.HashMap;

public interface IParameterGroup {
    Metric getMetric();
    String getJson();
    HashMap<String,String> getParameters();
}
