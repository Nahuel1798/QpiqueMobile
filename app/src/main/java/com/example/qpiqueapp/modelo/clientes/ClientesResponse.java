package com.example.qpiqueapp.modelo.clientes;

import java.util.List;

public class ClientesResponse {
    private int total;
    private List<Clientes> clientes;

    public int getTotal() {
        return total;
    }

    public List<Clientes> getClientes() {
        return clientes;
    }
}
