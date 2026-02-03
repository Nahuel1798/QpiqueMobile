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
    private MutableLiveData<Boolean> eliminado = new MutableLiveData<>(false);

    public BorrarClienteViewModel(@NonNull Application app) {
        super(app);
    }

    public MutableLiveData<Boolean> getEliminado() {
        return eliminado;
    }
    public void eliminarCliente(int id) {
        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        ApiClient.getInmoServicio()
                .eliminarCliente(auth, id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        eliminado.setValue(response.isSuccessful());
                        }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        eliminado.setValue(false);
                    }
                });
    }
}
