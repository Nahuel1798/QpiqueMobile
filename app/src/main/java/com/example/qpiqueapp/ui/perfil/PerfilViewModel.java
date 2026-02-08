package com.example.qpiqueapp.ui.perfil;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.perfil.PerfilDto;
import com.example.qpiqueapp.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {

    private final MutableLiveData<PerfilDto> perfilLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public PerfilViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<PerfilDto> getPerfilLiveData() {
        return perfilLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void cargarPerfil(String token) {
        ApiClient.getInmoServicio()
                .getProfile("Bearer " + token)
                .enqueue(new Callback<PerfilDto>() {
                    @Override
                    public void onResponse(Call<PerfilDto> call, Response<PerfilDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            perfilLiveData.postValue(response.body());
                        } else {
                            errorLiveData.postValue("Error al cargar perfil (" + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(Call<PerfilDto> call, Throwable t) {
                        errorLiveData.postValue("Error de conexión");
                    }
                });
    }
}
