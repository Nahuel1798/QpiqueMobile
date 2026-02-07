package com.example.qpiqueapp.modelo.productos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Productos implements Serializable {
    private int id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private double precio;
    private int stock;
    private int cantidad;
    private String CategoriaNombre;

    @SerializedName("categoriaId")
    private int CategoriaId;

    public Productos() {

    }

    public Productos(int id, String nombre, String descripcion, String imagenUrl, double precio, int stock, String categoriaNombre, int categoriaId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
        this.stock = stock;
        this.cantidad = cantidad;
        CategoriaNombre = categoriaNombre;
        CategoriaId  = categoriaId;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getCategoriaNombre() {
        return CategoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        CategoriaNombre = categoriaNombre;
    }

    public int getCategoriaId() {
        return CategoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        CategoriaId = categoriaId;
    }
}
