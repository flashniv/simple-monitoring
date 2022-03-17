package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric.exporter;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.queue.QueueElement;
import ua.com.serverhelp.simplemonitoring.queue.itemprocessor.SumItemProcessor;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/apiv1/metric/exporter/nginx-access-log")
public class NginxAccessLogMetricRest extends AbstractMetricRest{
    @Autowired
    private Storage storage;

    @PostMapping("/")
    public ResponseEntity<String> receiveData(
            @RequestBody String data
    ) {
        String inputData = URLDecoder.decode(data, StandardCharsets.UTF_8);
        String[] inputs = inputData.split("\n");

        Instant timestamp=Instant.now();

        for (String input : inputs) {
            if (isAllowedMetric(input)) {
                try {
                    getInputQueue().add(timestamp+";exporter."+input);
                    input = Pattern.compile("(.*)\\{.*").matcher(input).replaceFirst("$1");
                    addTrigger("exporter."+input);
                } catch (NumberFormatException e) {
                    Sentry.captureException(e);
                    log.warn("NodeMetricRest::receiveData number format error " + input);
                    return ResponseEntity.badRequest().body("number format error " + input);
                } catch (IllegalStateException | IndexOutOfBoundsException e) {
                    Sentry.captureException(e);
                    log.warn("NodeMetricRest::receiveData regexp match error " + input);
                    return ResponseEntity.badRequest().body("regexp match error " + input);
                }
            }
        }

        return ResponseEntity.ok().body("Success");
    }

    @Override
    protected boolean createTriggers(String pathPart) {
        //create trigger for LA
        Metric metric=storage.getOrCreateMetric(pathPart);
        storage.createIfNotExistTrigger(pathPart+".GET.5XX-errors","ua.com.serverhelp.simplemonitoring.entities.trigger.NginxAccessLog500Checker",storage.getOrCreateParameterGroup(metric,"{\"code\":\"5XX\",\"method\":\"GET\"}"));
        storage.createIfNotExistTrigger(pathPart+".POST.5XX-errors","ua.com.serverhelp.simplemonitoring.entities.trigger.NginxAccessLog500Checker",storage.getOrCreateParameterGroup(metric,"{\"code\":\"5XX\",\"method\":\"POST\"}"));
        storage.createIfNotExistTrigger(pathPart+".GET.4XX-errors","ua.com.serverhelp.simplemonitoring.entities.trigger.NginxAccessLog400Checker",storage.getOrCreateParameterGroup(metric,"{\"code\":\"4XX\",\"method\":\"GET\"}"));
        storage.createIfNotExistTrigger(pathPart+".POST.4XX-errors","ua.com.serverhelp.simplemonitoring.entities.trigger.NginxAccessLog400Checker",storage.getOrCreateParameterGroup(metric,"{\"code\":\"4XX\",\"method\":\"POST\"}"));
        return true;
    }

    @Override
    protected String[] getAllowedMetrics() {
        return new String[]{
                "nginx_access_log"
        };
    }

    @Override
    protected void setItemProcessors(QueueElement queueElement) {
        JSONObject parameters=new JSONObject(queueElement.getJson());
        String code= parameters.getString("code");
        if (code.charAt(0)=='4'){
            parameters.put("code", "4XX");
            queueElement.addItemProcessor(new SumItemProcessor(queueElement.getPath(), parameters.toString(), queueElement.getTimestamp()));
        }
        if (code.charAt(0)=='5'){
            parameters.put("code", "5XX");
            queueElement.addItemProcessor(new SumItemProcessor(queueElement.getPath(), parameters.toString(), queueElement.getTimestamp()));
        }
    }
}
