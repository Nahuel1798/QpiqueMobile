package com.example.qpiqueapp.ui.ventas;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.qpiqueapp.modelo.productos.Productos;
import com.example.qpiqueapp.modelo.venta.DetalleVenta;
import com.example.qpiqueapp.modelo.venta.VentaActualizada;
import com.example.qpiqueapp.modelo.venta.VentaResponse;
import com.example.qpiqueapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarVentaViewModel extends AndroidViewModel {

    private final MutableLiveData<VentaResponse> ventaActualizada = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<List<DetalleVenta>> detalles = new MutableLiveData<>(new ArrayList<>());
    private int ventaId= 0;

    public EditarVentaViewModel(@NonNull Application application) {
        super(application);
    }

    // Observables
    public LiveData<VentaResponse> getVentaActualizada() {
        return ventaActualizada;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }
    public void setVentaId(int id) {
        this.ventaId = id;
    }

    public int getVentaId() {
        return ventaId;
    }

    public LiveData<List<DetalleVenta>> getDetalles() {
        return detalles;
    }

    // Se llama UNA sola vez al abrir una venta existente
    public void setDetalles(List<DetalleVenta> lista) {
        detalles.setValue(new ArrayList<>(lista));
    }

    // Agrega productos seleccionados
    public void agregarProductos(List<Productos> productos) {

        List<DetalleVenta> actuales = new ArrayList<>(detalles.getValue());

        for (Productos p : productos) {

            DetalleVenta existente = null;
            for (DetalleVenta d : actuales) {
                if (d.getProductoId() == p.getId()) {
                    existente = d;
                    break;
                }
            }

            if (existente != null) {
                existente.setCantidad(existente.getCantidad() + 1);
            } else {
                DetalleVenta d = new DetalleVenta();
                d.setProductoId(p.getId());
                d.setCantidad(1);
                d.setCantidadOriginal(0);
                d.setPrecioUnitario(p.getPrecio());
                d.setProductoNombre(p.getNombre());
                d.setImagenUrl(p.getImagenUrl());
                d.setStock(p.getStock());
                actuales.add(d);
            }
        }

        detalles.setValue(actuales);
    }

    public void eliminarDetalle(DetalleVenta detalle) {
        List<DetalleVenta> lista = new ArrayList<>(detalles.getValue());

        lista.removeIf(d ->
                d.getProductoId() == detalle.getProductoId()
        );

        detalles.setValue(lista);
    }

    // Guardar venta
    public void editarVenta(int ventaId) {

        List<DetalleVenta> lista = detalles.getValue();

        if (lista == null || lista.isEmpty()) {
            error.setValue("La venta debe tener al menos un producto");
            return;
        }

        loading.setValue(true);

        VentaActualizada dto = new VentaActualizada();
        dto.setDetalles(lista);

        String token = ApiClient.leerToken(getApplication());

        ApiClient.getInmoServicio()
                .editarVenta("Bearer " + token, ventaId, dto)
                .enqueue(new Callback<VentaResponse>() {

                    @Override
                    public void onResponse(Call<VentaResponse> call, Response<VentaResponse> response) {
                        loading.postValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            ventaActualizada.postValue(response.body());
                        } else {

                            try {
                                String errorJson = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : "ErrorBody null";

                                Log.e("EDITAR_VENTA_ERROR", "Código: " + response.code());
                                Log.e("EDITAR_VENTA_ERROR", "Error JSON: " + errorJson);

                                error.postValue(errorJson);

                            } catch (Exception e) {
                                Log.e("EDITAR_VENTA_ERROR", "Error leyendo errorBody", e);
                                error.postValue("Error al procesar la respuesta");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<VentaResponse> call, Throwable t) {
                        loading.postValue(false);
                        error.postValue("Error de conexión: " + t.getMessage());
                    }
                });
    }

    public void limpiar() {
        detalles.setValue(new ArrayList<>());
        ventaId = 0;
        ventaActualizada.setValue(null);
        error.setValue(null);
    }
}

