package ua.com.serverhelp.simplemonitoring.alerter;

import ua.com.serverhelp.simplemonitoring.utils.httpdriver.HttpDriver;
import ua.com.serverhelp.simplemonitoring.utils.httpdriver.HttpResponse;
import ua.com.serverhelp.simplemonitoring.utils.httpdriver.SimpleHttpDriver;

import java.io.IOException;

/**
 * Class for send messages into TG
 */
public class SimpleTelegramBot{
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

    /**
     * Function for send message to any chat
     *
     * @param text text of message
     * @return HttpResponse object for check state
     */
    public HttpResponse sendMessage(String text) throws IOException{
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

        return httpResponse;
    }

//    public void printAlert(Alert alert) {
//        if (alertFilters.matchFilters(alert)) return;
//
//        if(alert.getStopDate()==null){
//            printMessage("<b>ERR "+alert.getTrigger().getDescription()+" in path "+alert.getTrigger().getHost()+"</b>\non event time "+alert.getStartDate());
//        }else {
//            printMessage("<b>OK "+alert.getTrigger().getDescription()+" in path "+alert.getTrigger().getHost()+"</b>\non event time "+alert.getStopDate());
//        }
//    }
}