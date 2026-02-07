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
    public MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> eliminado = new MutableLiveData<>(false);
    public MutableLiveData<String> mensaje = new MutableLiveData<>();

    public BorrarClienteViewModel(@NonNull Application app) {
        super(app);
    }

    public void eliminarCliente(int id) {
        loading.setValue(true);

        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        ApiClient.getInmoServicio()
                .eliminarCliente(auth, id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        loading.setValue(false);

                        if (response.isSuccessful()) {
                            eliminado.setValue(true);
                            mensaje.setValue("Cliente eliminado correctamente");
                            return;
                        }

                        eliminado.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loading.setValue(false);
                        eliminado.setValue(false);
                        mensaje.setValue("Error de conexión con el servidor");
                    }
                });
    }
}
