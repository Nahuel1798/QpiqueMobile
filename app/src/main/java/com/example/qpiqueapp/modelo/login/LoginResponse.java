package com.example.qpiqueapp.modelo.login;

import com.example.qpiqueapp.modelo.usuarios.User;

public class LoginResponse {
    private String token;
    private User user;

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
