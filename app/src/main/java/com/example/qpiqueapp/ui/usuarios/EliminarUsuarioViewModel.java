package com.example.qpiqueapp.ui.usuarios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EliminarUsuarioViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> eliminado = new MutableLiveData<>(false);
    public EliminarUsuarioViewModel(@NonNull Application app) {
        super(app);
    }
    public MutableLiveData<Boolean> getEliminado() {
        return eliminado;
    }
    public void eliminarUsuario(String id) {
        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;
        ApiClient.getInmoServicio()
                .eliminarUsuario(auth, id)
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
