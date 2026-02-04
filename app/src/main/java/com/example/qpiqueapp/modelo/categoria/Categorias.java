package com.example.qpiqueapp.modelo.categoria;


import java.io.Serializable;

public class Categorias implements Serializable {

    private int id;
    private String nombre;
    private boolean estado;
    private String imagenUrl;


    public Categorias(int id, String nombre, boolean estado, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.imagenUrl = imagenUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
