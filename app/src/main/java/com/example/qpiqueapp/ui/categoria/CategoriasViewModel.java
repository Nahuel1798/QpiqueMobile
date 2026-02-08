package com.example.qpiqueapp.ui.categoria;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.categoria.Categorias;
import com.example.qpiqueapp.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriasViewModel extends AndroidViewModel {
    // MutableLiveData
    private final MutableLiveData<List<Categorias>> listaCategorias = new MutableLiveData<>();
    private final MutableLiveData<Categorias> categoriaSeleccionadaEditar = new MutableLiveData<>();
    private final MutableLiveData<Categorias> categoriaSeleccionadaEliminar = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navegarCrearCategoria = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public CategoriasViewModel(@NonNull Application application) {
        super(application);
    }

    // LiveData publicos
    public LiveData<List<Categorias>> getListaCategorias() { return listaCategorias; }
    public LiveData<Categorias> getCategoriaSeleccionadaEditar() { return categoriaSeleccionadaEditar; }
    public LiveData<Categorias> getCategoriaSeleccionadaEliminar() { return categoriaSeleccionadaEliminar; }
    public LiveData<Boolean> getNavegarCrearCategoria() { return navegarCrearCategoria; }
    public LiveData<String> getMensajeError() { return mensajeError; }

    // Cargar categorias
    public void cargarCategorias() {
        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensajeError.setValue("Error: token de autenticación no encontrado.");
            return;
        }
        ApiClient.getInmoServicio()
                .getCategoria("Bearer " + token)
                .enqueue(new Callback<List<Categorias>>() {
                    @Override
                    public void onResponse(Call<List<Categorias>> call, Response<List<Categorias>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            listaCategorias.setValue(response.body());
                        } else {
                            mensajeError.setValue("Error al cargar categorías: respuesta no exitosa");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Categorias>> call, Throwable t) {
                        mensajeError.setValue("Error al cargar categorías: " + t.getMessage());
                    }
                });
    }

    // Eventos
    public void seleccionarEditar(Categorias categoria) {
        categoriaSeleccionadaEditar.setValue(categoria);
    }

    public void seleccionarEliminar(Categorias categoria) {
        categoriaSeleccionadaEliminar.setValue(categoria);
    }

    public void nuevoCategoria() {
        navegarCrearCategoria.setValue(true);
    }

    // reset
    public void navegarCompletado() {
        navegarCrearCategoria.setValue(false);
        categoriaSeleccionadaEditar.setValue(null);
        categoriaSeleccionadaEliminar.setValue(null);
    }
    public void mensajeErrorVisto() {
        mensajeError.setValue(null);
    }
}
