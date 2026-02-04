package com.example.qpiqueapp.modelo.productos;

public class ItemCarrito {
    private Productos producto;
    private int cantidad;

    public ItemCarrito(Productos producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;}

    // Getters y Setters para ambos campos
    public Productos getProducto() { return producto; }

    public void setProducto(Productos producto) { this.producto = producto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}