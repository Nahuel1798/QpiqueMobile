package com.example.qpiqueapp.ui.ventas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.clientes.Clientes;
import com.example.qpiqueapp.modelo.productos.Productos;
import com.example.qpiqueapp.modelo.venta.VentaCrearRequest;
import com.example.qpiqueapp.modelo.venta.DetalleVentaRequest;
import com.example.qpiqueapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearVentaViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>();
    private final MutableLiveData<Boolean> ventaCreada = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private Clientes cliente;

    public CrearVentaViewModel(@NonNull Application application) {
        super(application);
    }

    // Getter
    public LiveData<Boolean> getVentaCreada() {
        return ventaCreada;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    public Clientes getCliente() {
        return cliente;
    }




    // Metodos
    public void crearVenta(List<Productos> carrito) {
        ventaCreada.setValue(false);

        if (cliente == null || carrito == null || carrito.isEmpty()) {
            error.setValue("Debe seleccionar un cliente y al menos un producto");
            return;
        }

        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            error.setValue("Error de autenticación. Por favor, inicie sesión de nuevo.");
            return;
        }

        cargando.setValue(true);

        // Detalle de la venta
        List<DetalleVentaRequest> detalles = new ArrayList<>();
        for (Productos p : carrito) {
            detalles.add(new DetalleVentaRequest(p.getId(), p.getCantidad()));
        }

        VentaCrearRequest request = new VentaCrearRequest(cliente.getId(), detalles);

        ApiClient.getInmoServicio()
                .crearVenta("Bearer " + token, request)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        cargando.setValue(false);

                        if (response.isSuccessful()) {
                            ventaCreada.setValue(true);
                        } else {
                            String errorBodyStr = "Respuesta de error no disponible.";
                            try {
                                if (response.errorBody() != null) {
                                    errorBodyStr = response.errorBody().string();
                                }
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }
                            error.setValue("Error al crear la venta. Código: " + response.code() + " - " + errorBodyStr);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        cargando.setValue(false);
                        error.setValue("Error de conexión: " + t.getMessage());
                    }
                });
    }
    // Resetear
    public void resetVentaCreada() {
        ventaCreada.setValue(false);
    }
}
