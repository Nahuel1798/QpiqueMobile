package com.example.qpiqueapp.ui.categoria;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EliminarCategoriaViewModel extends AndroidViewModel {
    public MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> eliminado = new MutableLiveData<>(false);
    public MutableLiveData<String> mensaje = new MutableLiveData<>();

    public EliminarCategoriaViewModel(@NonNull Application app) {
        super(app);
    }

    public void eliminarCategoria(int id) {
        loading.setValue(true);

        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        ApiClient.getInmoServicio()
                .eliminarCategoria(auth, id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                       loading.setValue(false);

                       if (response.isSuccessful()) {
                           eliminado.setValue(true);
                           mensaje.setValue("Categoria eliminada correctamente");
                           return;
                       }

                       eliminado.setValue(false);
                       try {
                           if (response.errorBody() != null) {
                               mensaje.setValue(response.errorBody().string());
                               return;
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                           if (e.getMessage() != null) {
                               mensaje.setValue(e.getMessage());
                           }
                       }
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
