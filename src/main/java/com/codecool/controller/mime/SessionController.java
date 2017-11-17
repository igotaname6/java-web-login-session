package com.codecool.controller.mime;

import com.codecool.model.User;

import java.util.Map;

public class SessionController {

    public User createUser(Map<String, String> formData){
        User user = new User(formData.get("username"), formData.get("password"));

    }

}
