package com.example.qpiqueapp.ui.clientes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BorrarClienteViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navegarAtras = new MutableLiveData<>();

    public BorrarClienteViewModel(@NonNull Application app) {
        super(app);
    }

    // Getters

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<String> getMensaje() {
        return mensaje;
    }

    public MutableLiveData<Boolean> getNavegarAtras() {
        return navegarAtras;
    }

    // Acciones

    public void eliminarCliente(Integer id) {

        if (id == null || id <= 0) {
            mensaje.setValue("Cliente inválido");
            navegarAtras.setValue(true);
            return;
        }

        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensaje.setValue("Sesión no válida");
            navegarAtras.setValue(true);
            return;
        }

        loading.setValue(true);

        ApiClient.getInmoServicio()
                .eliminarCliente("Bearer " + token, id)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        loading.setValue(false);

                        if (response.isSuccessful()) {
                            mensaje.setValue("Cliente eliminado correctamente");
                            navegarAtras.setValue(true);
                        } else {
                            mensaje.setValue("Error al eliminar cliente (" + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loading.setValue(false);
                        mensaje.setValue("Error de conexión con el servidor");
                    }
                });
    }
}

