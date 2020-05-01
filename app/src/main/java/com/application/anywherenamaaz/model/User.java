package com.application.anywherenamaaz.model;

public class User {

    private String name;
    private String token;

    public User(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }
}
