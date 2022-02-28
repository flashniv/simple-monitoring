package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric.exporter;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/apiv1/metric/exporter/nginx-access-log")
public class NginxAccessLogMetricRest {
    @Autowired
    private MetricsQueue metricsQueue;
    @Autowired
    private Storage storage;

    /*
nginx_access_log.collaborator.balancer-kvm1.csgopedia_com{method="GET",code="200"} 141
nginx_access_log.collaborator.balancer-kvm1.csgopedia_com{method="GET",code="206"} 1
nginx_access_log.collaborator.balancer-kvm1.csgopedia_com{method="GET",code="404"} 6
nginx_access_log.collaborator.balancer-kvm1.csgopedia_com{method="GET",code="500"} 1
nginx_access_log.collaborator.balancer-kvm1.csgopedia_com{method="POST",code="200"} 1
nginx_access_log.collaborator.balancer-kvm1.csgopedia_com{method="POST",code="404"} 1
nginx_access_log.collaborator.balancer-kvm1.csgopedia_net{method="GET",code="200"} 1
nginx_access_log.collaborator.balancer-kvm1.fortbase_net{method="GET",code="200"} 1
nginx_access_log.collaborator.balancer-kvm2.artimg_info{method="GET",code="200"} 3
nginx_access_log.collaborator.balancer-kvm2.collaborator_pro{method="GET",code="101"} 69
nginx_access_log.collaborator.balancer-kvm2.collaborator_pro{method="GET",code="200"} 413
nginx_access_log.collaborator.balancer-kvm2.collaborator_pro{method="GET",code="302"} 5
nginx_access_log.collaborator.balancer-kvm2.collaborator_pro{method="GET",code="304"} 59
nginx_access_log.collaborator.balancer-kvm2.collaborator_pro{method="POST",code="200"} 84
nginx_access_log.collaborator.balancer-kvm2.collaborator_pro{method="POST",code="302"} 4
nginx_access_log.collaborator.balancer-kvm2.confluence_clbteam_com{method="GET",code="200"} 1
nginx_access_log.collaborator.balancer-kvm2.confluence_clbteam_com{method="GET",code="302"} 1
nginx_access_log.collaborator.balancer-kvm2.jenkins_clbteam_com{method="GET",code="200"} 2
nginx_access_log.collaborator.balancer-kvm2.jenkins_clbteam_com{method="GET",code="403"} 1
nginx_access_log.collaborator.balancer-kvm2.jenkins_clbteam_com{method="POST",code="200"} 1
nginx_access_log.collaborator.balancer-kvm2.jenkins_clbteam_com{method="POST",code="201"} 1
nginx_access_log.collaborator.balancer-kvm2.testwebinars_collaborator_pro{method="GET",code="503"} 1
nginx_access_log.collaborator.balancer-kvm2.tgsrv_collaborator_pro{method="GET",code="200"} 1
nginx_access_log.collaborator.balancer-kvm2.webinars_collaborator_pro{method="GET",code="200"} 4
nginx_access_log.collaborator.balancer-kvm3.conference_collaborator_pro{method="GET",code="200"} 1
nginx_access_log.collaborator.balancer-kvm3.conference_collaborator_pro{method="GET",code="302"} 1
nginx_access_log.collaborator.balancer-kvm3.grafana_clbteam_com{method="GET",code="400"} 4
nginx_access_log.collaborator.balancer-kvm3.jira_clbteam_com{method="GET",code="200"} 3
nginx_access_log.collaborator.balancer-kvm3.repo_clbteam_com{method="GET",code="200"} 75
nginx_access_log.collaborator.balancer-kvm3.repo_clbteam_com{method="GET",code="404"} 2
nginx_access_log.collaborator.balancer-kvm3.repo_clbteam_com{method="POST",code="200"} 11
nginx_access_log.collaborator.balancer-kvm3.repo_clbteam_com{method="POST",code="499"} 1
nginx_access_log.collaborator.balancer-kvm3.repo_clbteam_com{method="PUT",code="200"} 1
*/

    @PostMapping("/")
    public ResponseEntity<String> receiveData(
            @RequestBody String data
    ) {
        String inputData = URLDecoder.decode(data, StandardCharsets.UTF_8);
        String[] inputs = inputData.split("\n");

        for (String input : inputs) {
            MYLog.printAnywhere(input);
            if (isAllowedMetric(input)) {
                try {
                    processItem(input);
                } catch (NumberFormatException e) {
                    MYLog.printWarn("NodeMetricRest::receiveData number format error " + input);
                    return ResponseEntity.badRequest().body("number format error " + input);
                } catch (IllegalStateException | IndexOutOfBoundsException e) {
                    MYLog.printWarn("NodeMetricRest::receiveData regexp match error " + input);
                    return ResponseEntity.badRequest().body("regexp match error " + input);
                }
            }
        }
        //add triggers and calculate metrics
        //TODO release it
        //checkAdditionalConditions("exporter." + proj + ".blackbox." + siteId + ".");

        return ResponseEntity.ok().body("Success");
    }

    private void processItem(String input) throws IllegalStateException, IndexOutOfBoundsException, NumberFormatException {
        Double value;
        String parameters = "";
        //probe_dns_lookup_time_seconds 0.068937512
        input = input.replace("\r", "");
        input = Pattern.compile("(.*[0-9]e) ([0-9]+)$").matcher(input).replaceFirst("$1+$2");
        String[] parts;
        Pattern p = Pattern.compile("(.*)\\{(.*)} (.*)");
        Matcher m = p.matcher(input);
        if (m.matches()) {
            parts = new String[3];
            parts[0] = m.group(1);
            parameters = m.group(2);
            parts[2] = m.group(3);
        } else {
            parts = input.split(" ");
        }
        value = Double.valueOf(parts[parts.length - 1]);

        metricsQueue.putData(parts[0], parseParameterGroup(parameters), getOptionsByMetric(parts[0]), Instant.now(), value);
    }

//    private void checkAdditionalConditions(String pathPart) {
//        //create trigger for LA
//        Metric probeSuccess=storage.getOrCreateMetric(pathPart+"probe_success");
//        storage.createIfNotExistTrigger(pathPart+"probe_success","ua.com.serverhelp.simplemonitoring.entities.trigger.Last15minValuesChecker",storage.getOrCreateParameterGroup(probeSuccess,"{}"));
//        storage.createIfNotExistTrigger(pathPart+"probe_success","ua.com.serverhelp.simplemonitoring.entities.trigger.BooleanChecker",storage.getOrCreateParameterGroup(probeSuccess,"{}"));
//    }

    private String parseParameterGroup(String part) throws IllegalStateException, IndexOutOfBoundsException {
        JSONObject json = new JSONObject();
        String[] parameters = part.split(",");
        for (String parameter : parameters) {
            Pattern pattern = Pattern.compile("(.*)=\"(.*)\"");
            Matcher matcher = pattern.matcher(parameter);
            if (matcher.matches()) {
                json.put(matcher.group(1), matcher.group(2));
            }
        }
        return json.toString();
    }

    private boolean isAllowedMetric(String metric) {
        //nginx_access_log.collaborator.balancer-kvm1.csgopedia_com{method="GET",code="200"}
        return metric.matches("nginx_access_log.*method=.*,code=.*");
    }

    private String getOptionsByMetric(String metric) {
        JSONObject res = new JSONObject();
        return res.toString();
    }
}
