package ua.com.serverhelp.simplemonitoring.utils.httpdriver;

import java.util.HashMap;

public class DummyHttpDriver implements HttpDriver {
    private HttpResponse httpResponse;
    private String url="";
    private String additionalURL="";
    private HashMap params=new HashMap();

    public DummyHttpDriver() {
        httpResponse=new SimpleHttpResponse(200,"OK");
    }

    @Override
    public void setURL(String url) {
        this.url=url;
    }

    @Override
    public void setAdditionalURL(String url) {
        this.additionalURL=url;
    }

    @Override
    public HttpResponse sendPost(String additionUrl) {
        return httpResponse;
    }

    @Override
    public HttpResponse sendPost(String additionUrl, Object parameters) {
        return httpResponse;
    }

    @Override
    public HttpResponse sendPost() {
        return httpResponse;
    }

    @Override
    public HttpResponse sendGet() {
        return httpResponse;
    }

    @Override
    public HttpResponse sendGet(String additionUrl) {
        return httpResponse;
    }

    @Override
    public HttpResponse sendGet(String additionUrl, HashMap parameters) {
        return httpResponse;
    }

    @Override
    public void addParameter(String name, Object value) {
        params.put(name,value);
    }
    public HashMap getParams(){
        return params;
    }
    public String getUrl(){
        return url+"/"+additionalURL;
    }
}
