package com.example.qpiqueapp.ui.productos;

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

public class ProductosViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Productos>> productosLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Categorias>> categoriasLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> total = new MutableLiveData<>(0);

    private final List<Productos> acumulados = new ArrayList<>();

    private Integer categoriaId = null;
    private String nombre = null;
    private int page = 1;
    private final int pageSize = 6;
    private boolean ultimaPagina = false;

    public ProductosViewModel(@NonNull Application application) {
        super(application);
    }

    // Getters
    public LiveData<List<Productos>> getListaProductos() {
        return productosLiveData;
    }

    public LiveData<List<Categorias>> getCategorias() {
        return categoriasLiveData;
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<Integer> getTotal() {
        return total;
    }

    // Acciones
    public void cargarInicial() {
        resetear();
        cargarProductos();
    }

    public void seleccionarCategoria(Integer nuevaCategoriaId) {
        categoriaId = nuevaCategoriaId;
        resetear();
        cargarProductos();
    }

    public void buscar(String texto) {
        nombre = (texto == null || texto.isEmpty()) ? null : texto.trim();
        resetear();
        cargarProductos();
    }

    public void cargarMas() {
        if (Boolean.TRUE.equals(cargando.getValue()) || ultimaPagina) return;
        page++;
        cargarProductos();
    }

    // Privados
    private void resetear() {
        page = 1;
        ultimaPagina = false;
        acumulados.clear();
        productosLiveData.setValue(new ArrayList<>());
    }

    public void cargarCategorias() {
        ApiClient.getInmoServicio()
                .getCategorias()
                .enqueue(new Callback<List<Categorias>>() {
                    @Override
                    public void onResponse(
                            Call<List<Categorias>> call,
                            Response<List<Categorias>> response) {

                        List<Categorias> lista = new ArrayList<>();

                        Categorias todas = new Categorias();
                        todas.setId(0);
                        todas.setNombre("Todas");
                        lista.add(todas);

                        if (response.isSuccessful() && response.body() != null) {
                            lista.addAll(response.body());
                        }

                        categoriasLiveData.setValue(lista);
                    }

                    @Override
                    public void onFailure(Call<List<Categorias>> call, Throwable t) {
                        categoriasLiveData.setValue(new ArrayList<>());
                    }
                });
    }

    private void cargarProductos() {
        cargando.setValue(true);

        ApiClient.getInmoServicio()
                .getProductos(categoriaId, nombre, page, pageSize)
                .enqueue(new Callback<ProductosResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProductosResponse> call,
                            Response<ProductosResponse> response) {

                        cargando.setValue(false);

                        if (!response.isSuccessful() || response.body() == null) return;

                        List<Productos> nuevos = response.body().getProductos();
                        total.setValue(response.body().getTotal());

                        if (nuevos == null || nuevos.isEmpty()) {
                            ultimaPagina = true;
                            return;
                        }

                        // Permite ver todos los productos por mas que pasemos de pagina
                        acumulados.addAll(nuevos);
                        productosLiveData.setValue(new ArrayList<>(acumulados));
                    }

                    @Override
                    public void onFailure(Call<ProductosResponse> call, Throwable t) {
                        cargando.setValue(false);
                    }
                });
    }
}
