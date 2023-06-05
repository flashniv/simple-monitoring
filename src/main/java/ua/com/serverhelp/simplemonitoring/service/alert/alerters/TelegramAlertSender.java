package ua.com.serverhelp.simplemonitoring.service.alert.alerters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TelegramAlertSender implements AlertSender {
    private String token;
    private String chatId;

    @Override
    public void initialize(String jsonParams) throws JsonProcessingException {
        var conf = new ObjectMapper().readTree(jsonParams);
        token = conf.get("token").asText();
        chatId = conf.get("chat_id").asText();
    }

    @Override
    public void sendMessage(Alert alert) throws Exception {
        if (token == null) {
            throw new Exception("TelegramAlertSender not initialize");
        }
        String URL = "https://api.telegram.org/";
        var objectMapper = new ObjectMapper();
        var json = objectMapper.createObjectNode();
        json.put("chat_id", chatId);
        json.put("text", getTextByAlert(alert));
        json.put("parse_mode", "HTML");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(json));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "bot" + token + "/sendMessage"))
                .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .POST(bodyPublisher)
                .build();
        HttpResponse<String> stringBodyHandler = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (stringBodyHandler.statusCode() != 200) {
            throw new Exception("Error send TG message " + token + " chatId" + chatId);
        }
    }

    private String getTextByAlert(Alert alert) {
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.systemDefault());
        String text1 = "<b>" + alert.getTriggerStatus().name() + ": " + alert.getTrigger().getName() + "</b>";
        String text2 = "on time " + formatter.format(alert.getAlertTimestamp());
        return text1 + "\n" + text2;
    }
}
