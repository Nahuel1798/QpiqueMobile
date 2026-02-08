package com.example.qpiqueapp.ui.ventas;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.clientes.Clientes;
import com.example.qpiqueapp.modelo.usuarios.User;
import com.example.qpiqueapp.modelo.venta.Ventas;
import com.example.qpiqueapp.modelo.venta.VentasPaginadasResponse;
import com.example.qpiqueapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentasViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Ventas>> ventasLiveData = new MutableLiveData<>(new ArrayList<>());
    private final List<Ventas> ventasAcumuladas = new ArrayList<>();

    private final MutableLiveData<Clientes> clienteSeleccionado = new MutableLiveData<>();
    private final MutableLiveData<User> usuarioSeleccionado = new MutableLiveData<>();

    private final MutableLiveData<String> filtroFecha = new MutableLiveData<>();
    private final MutableLiveData<String> filtroTexto = new MutableLiveData<>();

    private int currentPage = 1;
    private final int pageSize = 10;
    private boolean isLoading = false;
    private boolean lastPage = false;

    public VentasViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Ventas>> getVentas() {
        return ventasLiveData;
    }

    public LiveData<Clientes> getClienteSeleccionado() {
        return clienteSeleccionado;
    }


    public boolean isLoading() {
        return isLoading;
    }

    public boolean isLastPage() {
        return lastPage;
    }

    // Cliente
    public void setClienteSeleccionado(Clientes cliente) {
        clienteSeleccionado.setValue(cliente);
    }

    public void deseleccionarCliente() {
        clienteSeleccionado.setValue(null);
    }

    // Filtros
    public void setFechaFiltro(String fecha) {
        filtroFecha.setValue(fecha);
        reiniciarCarga();
    }

    public void buscar(String texto) {
        filtroTexto.setValue(texto == null || texto.trim().isEmpty() ? null : texto.trim());
        reiniciarCarga();
    }

    public void recargar() {
        reiniciarCarga();
    }


    private void reiniciarCarga() {
        currentPage = 1;
        lastPage = false;
        isLoading = false;

        ventasAcumuladas.clear();
        ventasLiveData.setValue(new ArrayList<>());

        cargarVentas();
    }

    public void cargarVentas() {

        if (isLoading || lastPage) return;
        isLoading = true;

        String token = "Bearer " + ApiClient.leerToken(getApplication());

        String fecha = filtroFecha.getValue();
        String texto = filtroTexto.getValue();
        Clientes clienteObj = clienteSeleccionado.getValue();
        String cliente = clienteObj != null ? String.valueOf(clienteObj.getId()) : null;
        User usuarioObj = usuarioSeleccionado.getValue();
        String usuario = usuarioObj != null ? String.valueOf(usuarioObj.getId()) : null;


        ApiClient.getInmoServicio()
                .getVentas(
                        token,
                        fecha,
                        texto,
                        cliente,
                        usuario,
                        null,
                        null,
                        currentPage,
                        pageSize
                )
                .enqueue(new Callback<VentasPaginadasResponse>() {
                    @Override
                    public void onResponse(Call<VentasPaginadasResponse> call,
                                           Response<VentasPaginadasResponse> response) {
                        isLoading = false;

                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e("VentasVM", "Respuesta inválida");
                            return;
                        }

                        List<Ventas> nuevas = response.body().getVentas();

                        if (nuevas == null || nuevas.isEmpty()) {
                            lastPage = true;
                            return;
                        }

                        ventasAcumuladas.addAll(nuevas);
                        ventasLiveData.setValue(new ArrayList<>(ventasAcumuladas));
                        currentPage++;
                    }

                    @Override
                    public void onFailure(Call<VentasPaginadasResponse> call, Throwable t) {
                        isLoading = false;
                        Log.e("VentasVM", "Error cargando ventas", t);
                    }
                });
    }

}

