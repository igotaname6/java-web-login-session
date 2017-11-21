package com.codecool.controller;

import com.codecool.dao.Dao;
import com.codecool.dao.DaoException;
import com.codecool.model.User;
import com.codecool.view.CustomTemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpCookie;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class LoginController implements HttpHandler{
    private final CustomTemplateEngine customTemplateEngine;
    private final Dao dao;

    public LoginController() throws DaoException {
        customTemplateEngine = new CustomTemplateEngine();
        dao = new Dao();
    }

    public void handle(HttpExchange httpExchange) throws IOException{

        String method = httpExchange.getRequestMethod();
        boolean isNewSession = isNewSession(httpExchange);

        if(isNewSession){

            if(method.equals("GET")){
                sendStaticHome(httpExchange);
            } else if(method.equals("POST")){
                establishNewSession(httpExchange);
            }
        } else {
            if (method.equals("GET")){
                showLoggedSession(httpExchange);
            } else if(method.equals("POST")) {
                stopSession(httpExchange);
            }
        }
    }

    private boolean isNewSession(HttpExchange httpExchange){
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");

        if(cookieStr == null){
            return true;
        }else{
            Boolean isSessionDeleted = null;
            try {
                cookieStr = parseCookies(cookieStr).get("sessionId");
                isSessionDeleted = !dao.IsSessionInDb(cookieStr);
            } catch (DaoException e) {
                e.printStackTrace();
            }
            return isSessionDeleted;
        }
    }

    public void establishNewSession(HttpExchange httpExchange) throws IOException{
        HttpCookie cookie;

        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();

        Map<String, String> formDataMap = parseFormData(formData);

        SessionController sessionController =  new SessionController();

        String sessionId = sessionController.setNewSession(formDataMap);
        cookie = new HttpCookie("sessionId", sessionId);
        httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());

        try {
            processWebFromTemplate(sessionId, httpExchange);
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> parseCookies(String cookies){
        Map<String, String> cookiesMap = new HashMap<>();

        String[] cookiesArray = cookies.split(";");

        for(String cookiePair : cookiesArray){
            String[] cookie = cookiePair.split("=");

            cookiesMap.put(cookie[0], cookie[1].replaceAll("\"", ""));
        }
        return cookiesMap;
    }

    private void sendStaticHome(HttpExchange httpExchange) throws IOException {
        String path = "./static/home.html";
        ClassLoader classLoader = getClass().getClassLoader();
        URL fileURL = classLoader.getResource(path);

        StaticController.sendFile(httpExchange, fileURL);
    }

    private Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> parsedForm = new HashMap<>();
        String[] pairs = formData.split("&");

        for(String pair: pairs){
            String decodedPair = new URLDecoder().decode(pair, "UTF-8");
            String [] singleSplitedPair = decodedPair.split("=");

            parsedForm.put(singleSplitedPair[0], singleSplitedPair[1]);
        }
        return parsedForm;
    }

    private void processWebFromTemplate(String sessionId, HttpExchange httpExchange) throws DaoException, IOException {
        User user = dao.getUserBySession(sessionId);

        String response = customTemplateEngine.createHtml(user);

        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void showLoggedSession(HttpExchange httpExchange) throws IOException {
        String cookie = httpExchange.getRequestHeaders().getFirst("Cookie");
        String sessionId = parseCookies(cookie).get("sessionId");
        try {
            processWebFromTemplate(sessionId, httpExchange);
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    private void stopSession(HttpExchange httpExchange) throws IOException {
        String cookie = httpExchange.getRequestHeaders().getFirst("Cookie");
        String sessionId = parseCookies(cookie).get("sessionId");
        try {
            dao.deleteSession(sessionId);
        } catch (DaoException e) {
            e.printStackTrace();
        }
        sendStaticHome(httpExchange);
    }
}
