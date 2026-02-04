package com.example.qpiqueapp.ui.ventas;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.clientes.Clientes;
import com.example.qpiqueapp.modelo.venta.Ventas;
import com.example.qpiqueapp.modelo.venta.VentasPaginadasResponse;
import com.example.qpiqueapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentasViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Ventas>> ventasLiveData = new MutableLiveData<>();
    private final List<Ventas> ventasAcumuladas = new ArrayList<>();
    private final MutableLiveData<Clientes> clienteSeleccionado = new MutableLiveData<>();

    private final MutableLiveData<String> filtroFecha = new MutableLiveData<>();
    private final MutableLiveData<String> filtroTexto = new MutableLiveData<>();

    private int currentPage = 1;
    private final int pageSize = 10;
    private boolean isLoading = false;
    private boolean lastPage = false;

    public VentasViewModel(@NonNull Application application) {
        super(application);
    }

    // LiveData getters
    public LiveData<List<Ventas>> getVentas() { return ventasLiveData; }
    public LiveData<Clientes> getClienteSeleccionado() { return clienteSeleccionado; }
    public LiveData<String> getFiltroFecha() { return filtroFecha; }
    public LiveData<String> getFiltroTexto() { return filtroTexto; }

    // Selección de cliente
    public void setClienteSeleccionado(Clientes cliente) { clienteSeleccionado.setValue(cliente); }
    public void deseleccionarCliente() { clienteSeleccionado.setValue(null); }

    public boolean isLoading() { return isLoading; }
    public boolean isLastPage() { return lastPage; }

    // Filtros
    public void setFechaFiltro(String fecha) {
        filtroFecha.setValue(fecha);
        reiniciarCarga();
    }

    public void setTextoFiltro(String texto) {
        filtroTexto.setValue(texto);
        reiniciarCarga();
    }

    public void limpiarFiltros() {
        filtroFecha.setValue(null);
        filtroTexto.setValue(null);
        reiniciarCarga();
    }

    // Reinicia paginación
    public void reiniciarCarga() {
        currentPage = 1;
        lastPage = false;
        ventasAcumuladas.clear();
        ventasLiveData.setValue(new ArrayList<>());
        cargarVentas();
    }

    // Metodo para buscar por texto
    public void buscar(String texto) {
        setTextoFiltro(texto == null ? "" : texto.trim());
    }

    // Carga ventas con paginación y filtros
    public void cargarVentas() {
        if (isLoading || lastPage) return;
        isLoading = true;

        String token = "Bearer " + ApiClient.leerToken(getApplication());
        String fecha = filtroFecha.getValue();
        String texto = filtroTexto.getValue();
        Clientes c = clienteSeleccionado.getValue();
        String cliente = c != null ? String.valueOf(c.getId()) : null;

        ApiClient.getInmoServicio()
                .getVentas(token, fecha, texto, cliente, null, null, currentPage, pageSize)
                .enqueue(new Callback<VentasPaginadasResponse>() {
                    @Override
                    public void onResponse(Call<VentasPaginadasResponse> call, Response<VentasPaginadasResponse> response) {
                        isLoading = false;

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getVentas() != null) {

                            List<Ventas> nuevas = response.body().getVentas();
                            if (nuevas.isEmpty()) {
                                lastPage = true;
                                return;
                            }

                            ventasAcumuladas.addAll(nuevas);
                            ventasLiveData.setValue(new ArrayList<>(ventasAcumuladas));
                            currentPage++;
                        }
                    }

                    @Override
                    public void onFailure(Call<VentasPaginadasResponse> call, Throwable t) {
                        isLoading = false;
                        Log.e("VentasVM", "Error cargando ventas", t);
                    }
                });
    }
}
