package ua.com.serverhelp.simplemonitoring.utils.httpdriver;

import java.util.HashMap;

public interface HttpDriver {

    void setURL(String url);

    void setAdditionalURL(String url);

    HttpResponse sendPost(String additionUrl);

    HttpResponse sendPost(String additionUrl, Object parameters);

    HttpResponse sendPost();

    HttpResponse sendGet(String additionUrl);

    HttpResponse sendGet();

    HttpResponse sendGet(String additionUrl, HashMap parameters);

    void addParameter(String name, Object value);
}
