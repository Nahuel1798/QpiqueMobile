package com.example.qpiqueapp.modelo.venta;

import java.util.List;

public class VentaActualizada {
    private List<DetalleVenta> detalles;
    private int clienteId;

    public VentaActualizada(){

    }

    public VentaActualizada(List<DetalleVenta> detalles, int clienteId) {
        this.detalles = detalles;
        this.clienteId = clienteId;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }
}
