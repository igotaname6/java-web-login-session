package com.codecool.controller;

import com.codecool.controller.mime.SessionController;
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

    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");


        if(method.equals("GET") && cookieStr == null) {
            sendStaticHome(httpExchange);

        } else if(method.equals("POST") && cookieStr == null){
            HttpCookie cookie;

            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();

            Map<String, String> formDataMap = parseFormData(formData);

            SessionController sessionController =  new SessionController();

            String sessionId = sessionController.setNewSession(formDataMap);
            System.out.println("sessionId: " + sessionId);
            cookie = new HttpCookie("sessionId", sessionId);
            httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());

            try {
                processWebFromTemplate(sessionId, httpExchange);
            } catch (DaoException e) {
                e.printStackTrace();
            }

        }
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
            System.out.println(decodedPair);
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
}
