package ua.com.serverhelp.simplemonitoring.service.alert.alerters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SlackAlertSender implements AlertSender {
    private String slackWebHook;
    private String slackChannel;
    private String slackUserName;

    @Override
    public void initialize(String jsonParams) throws JsonProcessingException {
        var conf = new ObjectMapper().readTree(jsonParams);
        slackWebHook = conf.get("slackWebHook").asText();
        slackChannel = conf.get("slackChannel").asText();
        slackUserName = conf.get("slackUserName").asText();
    }

    @Override
    public void sendMessage(Alert alert) throws IOException {
        var objectMapper = new ObjectMapper();
        var payload = objectMapper.createObjectNode();
        payload.put("channel", slackChannel);
        payload.put("username", slackUserName);
        payload.put("text", getText(alert));

        // create a client
        HttpClient client = HttpClient.newHttpClient();

        // create a request
        HttpRequest request = HttpRequest.newBuilder(URI.create(slackWebHook))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        // use the client to send the request
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new Exception("Slack response error " + response.statusCode() + " " + response.body());
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private String getText(Alert alert) {
        var trigger = alert.getTrigger();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault());

        return switch (alert.getTriggerStatus()) {
            case FAILED, UNCHECKED ->
                    "*FAIL: check trigger failed " + trigger.getName() + "*\non event time " + formatter.format(alert.getAlertTimestamp());
            case OK ->
                    "*OK: " + trigger.getName() + "*\non event time " + formatter.format(alert.getAlertTimestamp());
            case ERROR ->
                    "*ERR: " + trigger.getName() + "*\non event time " + formatter.format(alert.getAlertTimestamp());
        };
    }
}
