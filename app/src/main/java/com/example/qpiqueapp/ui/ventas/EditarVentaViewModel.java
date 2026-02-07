package com.example.qpiqueapp.ui.ventas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.qpiqueapp.modelo.productos.Productos;
import com.example.qpiqueapp.modelo.venta.DetalleVenta;
import com.example.qpiqueapp.modelo.venta.VentaActualizada;
import com.example.qpiqueapp.modelo.venta.VentaResponse;
import com.example.qpiqueapp.request.ApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarVentaViewModel extends AndroidViewModel {

    private final MutableLiveData<VentaResponse> ventaActualizada = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<List<DetalleVenta>> detalles = new MutableLiveData<>(new ArrayList<>());


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

    // PUT editar venta
    public void editarVenta(int ventaId, List<DetalleVenta> detalles) {

        if (detalles == null || detalles.isEmpty()) {
            error.setValue("La venta debe tener al menos un producto");
            return;
        }

        loading.setValue(true);

        VentaActualizada dto = new VentaActualizada();
        dto.setDetalles(detalles);

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
                            error.postValue(leerError(response));
                        }
                    }

                    @Override
                    public void onFailure(Call<VentaResponse> call, Throwable t) {
                        loading.postValue(false);
                        error.postValue("Error de conexión: " + t.getMessage());
                    }
                });
    }

    // Lee mensaje de error del backend
    private String leerError(Response<?> response) {
        try {
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null)
                return errorBody.string();
        } catch (IOException ignored) {}
        return "Error al actualizar la venta";
    }
}

