package com.codecool.controller.mime;

import com.codecool.dao.Dao;
import com.codecool.dao.DaoException;
import com.codecool.model.Session;
import com.codecool.model.User;

import java.util.Map;

public class SessionController {

    private Dao dao;

    public SessionController() {
        try {
            this.dao= new Dao();
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }


    private User createUser(Map<String, String> formData){
        User user = new User(formData.get("username"), formData.get("password"));
        return user;
    }

    public String setNewSession(Map<String, String> formData){
        User user = createUser(formData);
        Session session = new Session(user);

        try {
            dao.addSessionAndUser(session);
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return session.getId();
    }

}
