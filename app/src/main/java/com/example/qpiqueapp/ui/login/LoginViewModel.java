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

                            LoginResponse login = response.body();

                            ApiClient.guardarSesion(
                                    getApplication(),
                                    login.getToken(),
                                    login.getUser().getRoles()
                            );

                            Log.d("LoginViewModel", "Token: " + response.body().getToken());
                            Log.d("LoginViewModel", "Rol: " + response.body().getUser().getRoles());


                            mensaje.setValue("Bienvenido");
                            loginOk.setValue(true);
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
