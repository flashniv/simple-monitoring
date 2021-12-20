package ua.com.serverhelp.simplemonitoring.alerter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertFilters;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;
import ua.com.serverhelp.simplemonitoring.utils.httpdriver.HttpDriver;
import ua.com.serverhelp.simplemonitoring.utils.httpdriver.HttpResponse;
import ua.com.serverhelp.simplemonitoring.utils.httpdriver.SimpleHttpDriver;

/**
 * Class for send messages into TG
 */
@Component
public class SimpleTelegramBot implements Alerter{
    @Autowired
    private AlertFilters alertFilters;

    /**
     * Driver for use curl options
     */
    private final HttpDriver httpDriver;
    /**
     * Token for TG flashnivbot
     */
    @Value("${tg.chat.token}")
    private String token;
    /**
     * alert chat
     */
    @Value("${tg.chat.alert_chat_id}")
    private int alertChatId;
    /**
     * work chat
     */
    @Value("${tg.chat.work_chat_id}")
    private int workChatId;

    /**
     * @param httpDriver set HttpDriver realization
     * @param token set token for TG
     */
    public SimpleTelegramBot(HttpDriver httpDriver, String token) {
        this.httpDriver = httpDriver;
        this.token = token;
    }
    /**
     * @param httpDriver set HttpDriver realization
     */
    public SimpleTelegramBot(HttpDriver httpDriver) {
        this.httpDriver = httpDriver;
    }
    public SimpleTelegramBot() {
        httpDriver = new SimpleHttpDriver();
    }

    /**
     * Function for send message to any chat
     *
     * @param text text of message
     * @param chatId chat id
     * @return HttpResponse object for check state
     */
    public HttpResponse sendMessage(String text, int chatId){
        HttpResponse httpResponse;
        httpDriver.setURL("https://api.telegram.org");
        httpDriver.setAdditionalURL("/bot"+token+"/sendMessage");
        httpDriver.addParameter("chat_id",""+chatId);
        httpDriver.addParameter("text",text);
        httpDriver.addParameter("parse_mode","HTML");
        httpResponse=httpDriver.sendPost();

        return httpResponse;
    }

    /**
     * Function for send message to Alert chat
     *
     * @param text text of message
     * @return HttpResponse object for check state
     */
    public HttpResponse sendMessageToAlertChat(String text){
        return sendMessage(text,alertChatId);
    }

    /**
     * Function for send message to Work chat
     *
     * @param text text of message
     * @return HttpResponse object for check state
     */
    public HttpResponse sendMessageToWorkChat(String text){
        return sendMessage(text,workChatId);
    }

    @Override
    public void printMessage(String message) {
        HttpResponse response=sendMessageToAlertChat(message);
        if (response.getCode()!=200){
            MYLog.printError("SimpleTelegramBot::printMessage telegram response "+response.getCode()+" body="+response.getBody(),new Exception());
        }
    }

    @Override
    public void printInfo(String message) {
        sendMessageToWorkChat(message);
    }

    @Override
    public void printAlert(Alert alert) {
        if (alertFilters.matchFilters(alert)) return;

        if(alert.getStopDate()==null){
            printMessage("<b>ERR "+alert.getTrigger().getDescription()+" in path "+alert.getTrigger().getHost()+"</b>\non event time "+alert.getStartDate());
        }else {
            printMessage("<b>OK "+alert.getTrigger().getDescription()+" in path "+alert.getTrigger().getHost()+"</b>\non event time "+alert.getStopDate());
        }
    }
}