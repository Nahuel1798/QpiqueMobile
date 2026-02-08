package com.example.qpiqueapp.ui.ventas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.categoria.Categorias;
import com.example.qpiqueapp.modelo.productos.Productos;
import com.example.qpiqueapp.modelo.productos.ProductosResponse;
import com.example.qpiqueapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeleccionarVPVIewModel extends AndroidViewModel {
    private final MutableLiveData<List<Productos>> productosSeleccionados = new MutableLiveData<>();
    private final MutableLiveData<Productos> productoSeleccionado = new MutableLiveData<>();
    private final MutableLiveData<List<Categorias>> categorias = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> total = new MutableLiveData<>(0);

    private Integer categoriaId = null;
    private String nombre = null;
    private int page = 1;
    private int pageSize = 6;
    private boolean ultimaPagina = false;

    public SeleccionarVPVIewModel(@NonNull Application application) {
        super(application);
    }

    // Getter
    public LiveData<List<Productos>> getProductosSeleccionados() {
        return productosSeleccionados;
    }
    public LiveData<Productos> getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public LiveData<List<Categorias>> getCategorias() {
        return categorias;
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<Integer> getTotal() {
        return total;
    }

    // Acciones
    public void seleccionarCategoria(Integer nuevaCategoriaId) {
        categoriaId = nuevaCategoriaId;
        resetear();
        cargarProductos();
    }

    public void cargarMas() {
        if (Boolean.TRUE.equals(cargando.getValue()) || ultimaPagina) return;
        page++;
        cargarProductos();
    }

    public void buscar(String texto) {
        nombre = texto == null ? "" : texto.trim();
        resetear();
        cargarProductos();
    }

    // Metodos

    private void resetear() {
        page = 1;
        ultimaPagina = false;
        productosSeleccionados.setValue(new ArrayList<>());
    }

    public void cargarCategorias() {
        ApiClient.getInmoServicio()
                .getCategorias()
                .enqueue(new Callback<List<Categorias>>() {
                    @Override
                    public void onResponse(
                            Call<List<Categorias>> call,
                            Response<List<Categorias>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Categorias> lista = new ArrayList<>();
                            Categorias todas = new Categorias();
                            todas.setId(0);
                            todas.setNombre("Todas");
                            lista.add(todas);
                            lista.addAll(response.body());
                            categorias.setValue(lista);
                        } else {
                            categorias.setValue(new ArrayList<>());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Categorias>> call, Throwable t) {
                        categorias.setValue(new ArrayList<>());
                    }
                });
    }

    public void cargarProductos() {
        ApiClient.getInmoServicio()
                .getProductos(categoriaId, nombre, page, pageSize)
                .enqueue(new Callback<ProductosResponse>() {
                    @Override
                    public void onResponse(Call<ProductosResponse> call, Response<ProductosResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Productos> nuevos = response.body().getProductos();
                            total.setValue(response.body().getTotal());
                            if (nuevos == null || nuevos.isEmpty()) {
                                ultimaPagina = true;
                                return;
                            }
                            productosSeleccionados.setValue(nuevos);
                        }
                    }
                    @Override
                    public void onFailure(Call<ProductosResponse> call, Throwable t) {
                        productosSeleccionados.setValue(null);
                        cargando.setValue(false);
                    }
                });
    }
}
