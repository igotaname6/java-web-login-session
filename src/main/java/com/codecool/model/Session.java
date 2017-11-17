package com.codecool.model;

import java.util.UUID;

public class Session {

    private String id;
    private User user;

    public Session() {
        createNewSessionUUID();
    }

    public Session(String UUID){
        this.id = UUID;
    }

    public String getId() {
        return id;
    }

    private void createNewSessionUUID(){
        this.id = UUID.randomUUID().toString();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
