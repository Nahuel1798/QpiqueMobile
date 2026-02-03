package com.example.qpiqueapp.ui.productos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BorrarProductoViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> eliminado = new MutableLiveData<>(false);

    public BorrarProductoViewModel(@NonNull Application app) {
        super(app);
    }

    public MutableLiveData<Boolean> getEliminado() {
        return eliminado;
    }

    public void eliminarProducto(int id) {
        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        ApiClient.getInmoServicio()
                .eliminarProducto(auth, id)
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
