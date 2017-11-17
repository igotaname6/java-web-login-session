package com.codecool.controller.mime;

import com.codecool.model.Session;
import com.codecool.model.User;

import java.util.Map;

public class SessionController {

    private static User createUser(Map<String, String> formData){
        User user = new User(formData.get("username"), formData.get("password"));
        return user;
    }

    public static String setNewSession(Map<String, String> formData){
        User user = createUser(formData);
        Session session = new Session(user);

        return session.getId();
    }

}
