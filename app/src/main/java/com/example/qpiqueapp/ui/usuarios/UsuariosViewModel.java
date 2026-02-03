package com.example.qpiqueapp.ui.usuarios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.PerfilDto;
import com.example.qpiqueapp.modelo.UsuariosResponse;
import com.example.qpiqueapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuariosViewModel extends AndroidViewModel {
    private final MutableLiveData<List<PerfilDto>> listaUsuarios = new MutableLiveData<>();
    private final MutableLiveData<Integer> total = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();
    private final List<PerfilDto> acumulados = new ArrayList<>();
    private int page = 1;
    private int pageSize = 6;
    private boolean ultimaPagina = false;
    private String search = "";

    public UsuariosViewModel(@NonNull Application application) {
        super(application);
    }

    // LiveData publicos
    public LiveData<List<PerfilDto>> getListaUsuarios() { return listaUsuarios; }
    public LiveData<Integer> getTotal() { return total; }
    public LiveData<Boolean> getCargando() { return cargando; }
    public LiveData<String> getMensajeError() { return mensajeError; }

    public void cargarInicial() {
        resetear();
        cargarUsuarios();
    }
    public void cargarMas() {
        if (Boolean.TRUE.equals(cargando.getValue()) || ultimaPagina) return;
        page++;
        cargarUsuarios();
    }

    private void resetear() {
        page = 1;
        ultimaPagina = false;
        acumulados.clear();
        listaUsuarios.setValue(new ArrayList<>());
    }
    public void buscar(String texto) {
        search = texto == null ? "" : texto.trim();
        resetear();
        cargarUsuarios();
    }

    // Cargar usuarios
    public void cargarUsuarios() {
        cargando.setValue(true);
        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensajeError.setValue("Error: token de autenticación no encontrado.");
            cargando.setValue(false);
            return;
        }

        ApiClient.getInmoServicio()
                .getUsuario("Bearer " + token, page, pageSize, search)
                .enqueue(new Callback<UsuariosResponse>() {
                    @Override
                    public void onResponse(Call<UsuariosResponse> call, Response<UsuariosResponse> response) {
                        cargando.setValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            // ... tu lógica actual para una respuesta exitosa ...
                            List<PerfilDto> nuevos = response.body().getUsuarios();
                            total.setValue(response.body().getTotal());
                            if (nuevos == null || nuevos.isEmpty()) {
                                ultimaPagina = true;
                                // Si es la primera página y no hay resultados, asegúrate de que la lista esté vacía.
                                if (page == 1) {
                                    acumulados.clear();
                                    listaUsuarios.setValue(new ArrayList<>(acumulados));
                                }
                                return;
                            }
                            acumulados.addAll(nuevos);
                            listaUsuarios.setValue(new ArrayList<>(acumulados));
                        } else { // <-- AÑADIR ESTE BLOQUE ELSE
                            // Manejar respuestas de error del servidor (ej. 401, 403, 500)
                            String errorBody = "Error desconocido.";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string(); // Obtiene el mensaje de error del servidor
                                }
                            } catch (Exception e) {
                                // Ignorar excepción al leer el cuerpo del error
                            }
                            mensajeError.setValue("Error " + response.code() + ": " + errorBody);
                        }
                    }
                    @Override
                    public void onFailure(Call<UsuariosResponse> call, Throwable t) {
                        cargando.setValue(false); // <-- AÑADIR ESTA LÍNEA
                        mensajeError.setValue("Error al cargar usuarios: " + t.getMessage());
                    }
                });
    }
}
