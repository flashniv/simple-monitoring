package ua.com.serverhelp.simplemonitoring.utils.httpdriver;

import org.json.JSONObject;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class JsonHttpDriver implements HttpDriver {
    private String URL = "";
    private String additionUrl = "";
    private JSONObject args = new JSONObject();

    public JsonHttpDriver(String URL, String additionUrl, JSONObject args) {
        this.URL = URL;
        this.additionUrl = additionUrl;
        this.args = args;
    }
    public JsonHttpDriver(String URL, String additionUrl) {
        this.URL = URL;
        this.additionUrl = additionUrl;
    }

    public JsonHttpDriver() {
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
        return request();
    }

    @Override
    public HttpResponse sendPost(String additionUrl, Object parameters) {
        this.additionUrl = additionUrl;
        this.args = (JSONObject) parameters;
        return request();
    }

    @Override
    public HttpResponse sendPost() {
        return request();
    }

    @Override
    public HttpResponse sendGet() {
        return new SimpleHttpResponse(405, "Method not allowed!");
    }
    @Override
    public HttpResponse sendGet(String additionUrl) {
        return new SimpleHttpResponse(405, "Method not allowed!");
    }

    @Override
    public HttpResponse sendGet(String additionUrl, HashMap parameters) {
        return new SimpleHttpResponse(405, "Method not allowed!");
    }

    private HttpResponse request() {
        StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
        int responseCode = -1;
        String responseText = "";
        try {
            //create connection
            java.net.URL url = new URL(URL + additionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String requestMethod = "POST";
            con.setRequestMethod(requestMethod);
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");


            //putting data
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, StandardCharsets.UTF_8));
            writer.write(args.toString());
            writer.close();
            wr.flush();
            wr.close();
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

    @Override
    public void addParameter(String name, Object value) {
        args.put(name, value);
    }

}
