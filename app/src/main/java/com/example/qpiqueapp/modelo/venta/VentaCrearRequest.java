package com.example.qpiqueapp.modelo.venta;

import java.util.List;

public class VentaCrearRequest {
    public int clienteId;
    public List<DetalleVentaRequest> detalles;

    public VentaCrearRequest(int clienteId, List<DetalleVentaRequest> detalles) {
        this.clienteId = clienteId;
        this.detalles = detalles;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public List<DetalleVentaRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaRequest> detalles) {
        this.detalles = detalles;
    }
}
