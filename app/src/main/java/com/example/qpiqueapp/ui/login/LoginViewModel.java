package com.example.qpiqueapp.ui.login;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.MainActivity;
import com.example.qpiqueapp.modelo.LoginRequest;
import com.example.qpiqueapp.modelo.LoginResponse;
import com.example.qpiqueapp.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginOk = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public LiveData<Boolean> getLoginOk() {
        return loginOk;
    }

    public void Logueo(String usuario, String clave) {

        if (usuario.isEmpty() || clave.isEmpty()) {
            mensaje.setValue("Debe completar los campos");
            return;
        }

        LoginRequest loginRequest = new LoginRequest(usuario, clave);

        ApiClient.getInmoServicio()
                .login(loginRequest)
                .enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            ApiClient.guardartoken(
                                    getApplication(),
                                    response.body().getToken()
                            );

                            mensaje.setValue("Bienvenido");
                            loginOk.setValue(true); // 🔥 EVENTO
                        } else {
                            mensaje.setValue("Usuario o contraseña incorrectos");
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        mensaje.setValue(t.getMessage());
                    }
                });
    }
}
