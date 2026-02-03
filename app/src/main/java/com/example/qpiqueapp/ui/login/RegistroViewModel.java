package com.example.qpiqueapp.ui.login;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.RegisterRequest;
import com.example.qpiqueapp.request.ApiClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroViewModel extends AndroidViewModel {
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registroOk = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);

    public RegistroViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<String> getMensaje() {
        return mensaje;
    }
    public LiveData<Boolean> getRegistroOk() {
        return registroOk;
    }
    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public void Registro(String email, String password, String nombre, String apellido) {
        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
            mensaje.setValue("Complete todos los campos");
            return;
        }
        RegisterRequest registerRequest = new RegisterRequest(email, password, nombre, apellido);

        cargando.setValue(true);

        ApiClient.getInmoServicio()
                .crearRegistro(registerRequest)
                .enqueue(new Callback<ResponseBody>() {
                    private static final String TAG = "REGISTRO_VM";

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        cargando.setValue(false);

                        Log.d(TAG, "HTTP CODE: " + response.code());

                        if (response.isSuccessful()) {
                            Log.d(TAG, "Registro exitoso");
                            mensaje.setValue("Registro exitoso");
                            registroOk.setValue(true);
                        } else {
                            try {
                                String error = response.errorBody().string();
                                Log.e(TAG, "Error backend: " + error);
                                mensaje.setValue("Error en el registro");
                            } catch (Exception e) {
                                Log.e(TAG, "Error leyendo errorBody", e);
                                mensaje.setValue("Error en el registro");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        cargando.setValue(false);
                        mensaje.setValue("Error de conexión");
                    }
                });
    }
}
