package ua.com.serverhelp.simplemonitoring.utils.httpdriver;

import ua.com.serverhelp.simplemonitoring.utils.MYLog;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SimpleHttpDriver implements HttpDriver {
    private String URL = "";
    private String additionUrl = "";
    private HashMap args = new HashMap();
    private String requestMethod = "GET";

    public SimpleHttpDriver(String URL, String additionUrl, HashMap args, String requestMethod) {
        this.URL = URL;
        this.additionUrl = additionUrl;
        this.args = args;
        this.requestMethod = requestMethod;
    }
    public SimpleHttpDriver(String URL, String additionUrl, HashMap args) {
        this.URL = URL;
        this.additionUrl = additionUrl;
        this.args = args;
    }
    public SimpleHttpDriver(String URL, String additionUrl) {
        this.URL = URL;
        this.additionUrl = additionUrl;
    }

    public SimpleHttpDriver() {
    }

    @Override
    public void setURL(String url) {
        this.URL=url;
    }

    @Override
    public void setAdditionalURL(String url) {
        this.additionUrl=url;
    }

    @Override
    public HttpResponse sendPost(String additionUrl) {
        this.additionUrl = additionUrl;
        this.requestMethod = "POST";
        return request();
    }

    @Override
    public HttpResponse sendPost(String additionUrl, Object parameters) {
        this.additionUrl = additionUrl;
        this.args = (HashMap) parameters;
        this.requestMethod = "POST";
        return request();
    }

    @Override
    public HttpResponse sendPost() {
        this.requestMethod="POST";
        return request();
    }

    @Override
    public HttpResponse sendGet() {
        this.additionUrl = this.additionUrl + "?" + getParamsString(this.args);
        this.requestMethod = "GET";
        return request();
    }
    @Override
    public HttpResponse sendGet(String additionUrl) {
        this.additionUrl = additionUrl + "?" + getParamsString(this.args);
        this.requestMethod = "GET";
        return request();
    }

    @Override
    public HttpResponse sendGet(String additionUrl, HashMap parameters) {
        this.args = parameters;
        this.additionUrl = additionUrl + "?" + getParamsString(this.args);
        this.requestMethod = "GET";
        return request();
    }

    private HttpResponse request() {
        StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
        int responseCode = -1;
        String responseText = "";
        try {
            //create connection
            java.net.URL url = new URL(URL + additionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(requestMethod);
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");


            //putting data
            if (requestMethod.equals("POST")) {
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
                writer.write(getParamsString(args));
                writer.close();
                wr.flush();
                wr.close();
            }
            responseCode = con.getResponseCode();

            if (responseCode == 200) {
                //read output
                InputStream is = con.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                responseText = response.toString();
            }
            con.disconnect();
        } catch (IOException ex) {
            MYLog.printError("Curl error ",ex);
        }
        return new SimpleHttpResponse(responseCode, responseText);
    }

    private String getParamsString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    @Override
    public void addParameter(String name, Object value) {
        args.put(name, value);
    }

}
