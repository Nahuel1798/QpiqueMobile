package com.example.qpiqueapp.modelo.registro;

public class RegisterRequest {
    private String email;
    private String password;
    private String nombre;
    private String apellido;

    public RegisterRequest(String email, String password, String nombre, String apellido) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
    }
}
