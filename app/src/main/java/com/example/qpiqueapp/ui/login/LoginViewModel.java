package com.example.qpiqueapp.ui.login;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.login.LoginRequest;
import com.example.qpiqueapp.modelo.login.LoginResponse;
import com.example.qpiqueapp.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navegarMain = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    // Getters

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public LiveData<Boolean> getNavegarMain() {
        return navegarMain;
    }

    // Acciones

    public void loguear(String usuario, String clave) {

        if (usuario == null || usuario.trim().isEmpty()
                || clave == null || clave.trim().isEmpty()) {
            mensaje.setValue("Debe completar los campos");
            return;
        }

        loading.setValue(true);

        LoginRequest request = new LoginRequest(usuario, clave);

        ApiClient.getInmoServicio()
                .login(request)
                .enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(
                            Call<LoginResponse> call,
                            Response<LoginResponse> response) {

                        loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {

                            LoginResponse login = response.body();

                            ApiClient.guardarSesion(
                                    getApplication(),
                                    login.getToken(),
                                    login.getUser().getRoles()
                            );

                            Log.d("LoginVM", "Token: " + login.getToken());
                            Log.d("LoginVM", "Rol: " + login.getUser().getRoles());

                            mensaje.setValue("Bienvenido");
                            navegarMain.setValue(true);

                        } else {
                            mensaje.setValue("Usuario o contraseña incorrectos");
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        loading.setValue(false);
                        mensaje.setValue("Error de conexión");
                    }
                });
    }
}

