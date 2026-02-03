package com.example.qpiqueapp.modelo;

import java.util.List;

public class VentasPaginadasResponse {
    private List<Ventas> ventas;
    private int totalPages;
    private int currentPage;

    public List<Ventas> getVentas() {
        return ventas;
    }
    public void setVentas(List<Ventas> ventas) {
        this.ventas = ventas;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    public int getCurrentPage() {
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
