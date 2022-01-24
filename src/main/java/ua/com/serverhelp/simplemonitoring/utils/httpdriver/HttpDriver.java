package ua.com.serverhelp.simplemonitoring.utils.httpdriver;

import java.io.IOException;
import java.util.HashMap;

public interface HttpDriver {

    void setURL(String url);

    void setAdditionalURL(String url);

    HttpResponse sendPost(String additionUrl) throws IOException;

    HttpResponse sendPost(String additionUrl, HashMap<String,Object> parameters) throws IOException ;

    HttpResponse sendPost() throws IOException ;

    HttpResponse sendGet(String additionUrl) throws IOException ;

    HttpResponse sendGet() throws IOException ;

    HttpResponse sendGet(String additionUrl, HashMap<String,Object> parameters) throws IOException ;

    void addParameter(String name, Object value);
}
