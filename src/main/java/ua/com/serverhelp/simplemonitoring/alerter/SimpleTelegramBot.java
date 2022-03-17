package ua.com.serverhelp.simplemonitoring.alerter;

import org.json.JSONObject;
import ua.com.serverhelp.simplemonitoring.utils.httpdriver.HttpDriver;
import ua.com.serverhelp.simplemonitoring.utils.httpdriver.HttpResponse;
import ua.com.serverhelp.simplemonitoring.utils.httpdriver.SimpleHttpDriver;

import java.io.IOException;

/**
 * Class for send messages into TG
 */
public class SimpleTelegramBot implements AlertSender{
    /**
     * Driver for use curl options
     */
    private final HttpDriver httpDriver=new SimpleHttpDriver();
    /**
     * Token for TG flashnivbot
     */
    private final String token;
    /**
     * work chat
     */
    private final int chatId;

    public SimpleTelegramBot(String token, int chatId) {
        this.token = token;
        this.chatId = chatId;
    }
    public SimpleTelegramBot(JSONObject parameters) {
        this.token = parameters.getString("token");
        this.chatId = parameters.getInt("chatId");
    }

    /**
     * Function for send message to any chat
     *
     * @param text text of message
     * @return true if message was sent
     */
    @Override
    public boolean sendMessage(String text) throws IOException{
        HttpResponse httpResponse;
        httpDriver.setURL("https://api.telegram.org");
        httpDriver.setAdditionalURL("/bot"+token+"/sendMessage");
        httpDriver.addParameter("chat_id",""+chatId);
        httpDriver.addParameter("text",text);
        httpDriver.addParameter("parse_mode","HTML");
        httpResponse=httpDriver.sendPost();
        if(httpResponse.getCode()!=200){
            throw new IOException("Telegram return "+httpResponse.getCode()+" body"+httpResponse.getBody());
        }

        return true;
    }

}