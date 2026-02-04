package com.example.qpiqueapp.modelo.productos;

import java.util.List;

public class ProductosResponse {
    private int total;
    private List<Productos> productos;

    public int getTotal() {
        return total;
    }

    public List<Productos> getProductos() {
        return productos;
    }
}
