package com.example.qpiqueapp.modelo;


import java.io.Serializable;

public class Categorias implements Serializable {

    private int id;
    private String nombre;
    private boolean estado;
    private String imagenUrl;


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
    public boolean getEstado() {
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



    // IMPORTANTE PARA SPINNER
    @Override
    public String toString() {
        return nombre;
    }
}
