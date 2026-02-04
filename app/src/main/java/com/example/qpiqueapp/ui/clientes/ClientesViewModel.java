package com.example.qpiqueapp.ui.clientes;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.clientes.Clientes;
import com.example.qpiqueapp.modelo.clientes.ClientesResponse;
import com.example.qpiqueapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientesViewModel extends AndroidViewModel {

    private final  MutableLiveData<List<Clientes>> listaClientes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> total = new MutableLiveData<>(0);
    private final List<Clientes> acumulados = new ArrayList<>();
    private int page = 1;
    private int pageSize = 6;
    private boolean ultimaPagina = false;
    private String search = "";

    public ClientesViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<List<Clientes>> getListaClientes() {
        return listaClientes;
    }
    public LiveData<Boolean> getCargando() {
        return cargando;
    }
    public LiveData<Integer> getTotal() {
        return total;
    }
    public void cargarInicial() {
        resetear();
        cargarClientes();
    }
    public void cargarMas() {
        if (Boolean.TRUE.equals(cargando.getValue()) || ultimaPagina) return;
        page++;
        cargarClientes();
    }

    private void resetear() {
        page = 1;
        ultimaPagina = false;
        acumulados.clear();
        listaClientes.setValue(new ArrayList<>());
    }
    public void buscar(String texto) {
        search = texto == null ? "" : texto.trim();
        resetear();
        cargarClientes();
    }

    public void cargarClientes(){
        if (Boolean.TRUE.equals(cargando.getValue())) return;
        cargando.setValue(true);

        String token = ApiClient.leerToken(getApplication());
        String authHeader = "Bearer " + token;


        ApiClient.getInmoServicio()
                .getClientes(authHeader,page, pageSize, search)
                .enqueue(new Callback<ClientesResponse>() {
                    @Override
                    public void onResponse(Call<ClientesResponse> call, Response<ClientesResponse> response) {
                        cargando.setValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Clientes> nuevos = response.body().getClientes();
                            total.setValue(response.body().getTotal());
                            if (nuevos == null || nuevos.isEmpty()) {
                                ultimaPagina = true;
                                return;
                            }
                            acumulados.addAll(nuevos);
                            listaClientes.setValue(new ArrayList<>(acumulados));
                        }
                    }
                    @Override
                    public void onFailure(Call<ClientesResponse> call, Throwable t) {
                        cargando.setValue(false);
                        Log.e("ClientesVM", "Fallo en la llamada a la API", t);
                        Toast.makeText(getApplication(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}