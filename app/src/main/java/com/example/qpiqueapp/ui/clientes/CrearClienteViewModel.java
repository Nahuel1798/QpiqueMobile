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

    // Eventos
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navegarAtras = new MutableLiveData<>();

    // Estado
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public CrearClienteViewModel(@NonNull Application app) {
        super(app);
    }

    // Getter

    public MutableLiveData<String> getMensaje() {
        return mensaje;
    }

    public MutableLiveData<Boolean> getNavegarAtras() {
        return navegarAtras;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    // Acciones

    public void guardarCliente(String nombre, String apellido, String telefono, String email) {

        if (nombre == null || nombre.trim().isEmpty()) {
            mensaje.setValue("Ingrese un nombre");
            return;
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            mensaje.setValue("Ingrese un apellido");
            return;
        }

        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensaje.setValue("No hay sesión activa");
            return;
        }

        loading.setValue(true);

        ClientesCrearRequest request =
                new ClientesCrearRequest(nombre, apellido, telefono, email);

        ApiClient.getInmoServicio()
                .crearCliente("Bearer " + token, request)
                .enqueue(new Callback<Clientes>() {

                    @Override
                    public void onResponse(Call<Clientes> call, Response<Clientes> response) {
                        loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            mensaje.setValue("Cliente creado: " + response.body().getNombre());
                            navegarAtras.setValue(true);
                        } else {
                            mensaje.setValue("Error al crear cliente (" + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(Call<Clientes> call, Throwable t) {
                        loading.setValue(false);
                        mensaje.setValue("Error de conexión");
                    }
                });
    }
}

