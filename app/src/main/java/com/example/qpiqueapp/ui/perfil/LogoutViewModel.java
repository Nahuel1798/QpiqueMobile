package com.example.qpiqueapp.ui.perfil;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LogoutViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> mostrarDialogo = new MutableLiveData<>();
    private final MutableLiveData<Boolean> redirigirLogin = new MutableLiveData<>();

    public LogoutViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getMostrarDialogo() {
        return mostrarDialogo;
    }

    public LiveData<Boolean> getRedirigirLogin() {
        return redirigirLogin;
    }

    public void solicitarLogout() {
        mostrarDialogo.setValue(true);
    }

    public void cerrarSesion() {
        SharedPreferences sp =
                getApplication().getSharedPreferences("auth", Context.MODE_PRIVATE);

        sp.edit().clear().apply();
        redirigirLogin.setValue(true);
    }
}

