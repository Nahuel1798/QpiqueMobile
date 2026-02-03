package com.example.qpiqueapp.ui.perfil;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.PerfilDto;
import com.example.qpiqueapp.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {
    private final MutableLiveData<PerfilDto> perfilLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
     public PerfilViewModel(@NonNull Application application){
         super(application);
     }
     public LiveData<PerfilDto> getPerfilLiveData(){
         return perfilLiveData;
     }
     public LiveData<String> getErrorLiveData(){
         return errorLiveData;
     }
    public void cargarPerfil(String token){
        Context context = getApplication().getApplicationContext();
        ApiClient.getInmoServicio()
                .getProfile("Bearer " + token)
                .enqueue(new Callback<PerfilDto>(){
                    @Override
                    public void onResponse(Call<PerfilDto> call, Response<PerfilDto> response){
                        if(response.isSuccessful() && response.body() != null){
                            perfilLiveData.setValue(response.body());
                        }else{
                            errorLiveData.setValue("Error al cargar el perfil: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<PerfilDto> call, Throwable t){
                        errorLiveData.setValue(t.getMessage());
                    }
                });
    }

}
