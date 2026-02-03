package com.example.qpiqueapp.modelo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Ventas implements Serializable {
    private int id;
    private String fecha;
    private String clienteNombre;
    private double total;
    @SerializedName("detalles")
    private List<DetalleVenta> detalleVentas;

    public Ventas(int id, String fecha, String clienteNombre, double total, List<DetalleVenta> detalleVentas) {
        this.id = id;
        this.fecha = fecha;
        this.clienteNombre = clienteNombre;
        this.total = total;
        this.detalleVentas = detalleVentas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<DetalleVenta> getDetalleVentas() {
        return detalleVentas;
    }

    public void setDetalleVentas(List<DetalleVenta> detalleVentas) {
        this.detalleVentas = detalleVentas;
    }
}
