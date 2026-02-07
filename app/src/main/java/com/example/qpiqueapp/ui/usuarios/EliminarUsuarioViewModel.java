package com.example.qpiqueapp.ui.usuarios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.request.ApiClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EliminarUsuarioViewModel extends AndroidViewModel {

    public MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> eliminado = new MutableLiveData<>(false);
    public MutableLiveData<String> mensaje = new MutableLiveData<>();

    public EliminarUsuarioViewModel(@NonNull Application app) {
        super(app);
    }

    public void eliminarUsuario(String id) {
        loading.setValue(true);

        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        ApiClient.getInmoServicio()
                .eliminarUsuario(auth, id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        loading.setValue(false);

                        if (response.isSuccessful()) {
                            eliminado.setValue(true);
                            mensaje.setValue("Usuario eliminado correctamente");
                            return;
                        }

                        eliminado.setValue(false);

                        try {
                            if (response.errorBody() != null) {
                                JSONObject obj =
                                        new JSONObject(response.errorBody().string());

                                if (obj.has("message")) {
                                    mensaje.setValue(obj.getString("message"));
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mensaje.setValue("No se pudo eliminar el usuario");
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
