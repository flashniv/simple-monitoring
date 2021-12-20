package ua.com.serverhelp.simplemonitoring.utils.httpdriver;

public class SimpleHttpResponse implements HttpResponse {
    private int code;
    private String text;

    public SimpleHttpResponse(int code, String text) {
        this.code = code;
        this.text = text;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getBody() {
        return text;
    }
}
