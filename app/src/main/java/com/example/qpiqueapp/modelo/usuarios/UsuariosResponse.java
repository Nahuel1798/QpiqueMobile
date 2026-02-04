package com.example.qpiqueapp.modelo.usuarios;

import com.example.qpiqueapp.modelo.perfil.PerfilDto;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UsuariosResponse {
    @SerializedName("total")
    private int total;
    @SerializedName("users")
    private List<PerfilDto> usuarios;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<PerfilDto> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<PerfilDto> usuarios) {
        this.usuarios = usuarios;
    }
}
