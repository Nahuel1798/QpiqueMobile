package com.example.qpiqueapp.ui.clientes;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.qpiqueapp.modelo.clientes.Clientes;
import com.example.qpiqueapp.modelo.clientes.ClientesCrearRequest;
import com.example.qpiqueapp.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearClienteViewModel extends AndroidViewModel {
    // Estados
    private final MutableLiveData<Clientes> clienteCreado = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public CrearClienteViewModel(@NonNull Application app) {
        super(app);
    }

    public MutableLiveData<Clientes> getClienteCreado() {
        return clienteCreado;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }
    public void cargarCliente(String nombre, String apellido, String telefono, String email){
        if (nombre == null || nombre.trim().isEmpty()) {
            error.setValue("Ingrese un nombre");
            return;
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            error.setValue("Ingrese un apellido");
            return;
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            error.setValue("Ingrese un telefono");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            error.setValue("Ingrese un email");
            return;
        }
        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            error.setValue("No hay sesión activa");
            return;
        }
        ClientesCrearRequest request = new ClientesCrearRequest(nombre, apellido, telefono, email);
        ApiClient.getInmoServicio()
                .crearCliente("Bearer " + token, request)
                .enqueue(new Callback<Clientes>() {
                    @Override
                    public void onResponse(Call<Clientes> call, Response<Clientes> response) {
                        if (response.isSuccessful()) {
                            clienteCreado.setValue(response.body());
                        } else {
                            error.setValue("Error al crear cliente (" + response.code() + ")");
                        }
                    }
                    @Override
                    public void onFailure(Call<Clientes> call, Throwable t) {
                        error.setValue(t.getMessage());
                    }
                });


    }
}
