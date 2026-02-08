package com.example.qpiqueapp.ui.usuarios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.perfil.PerfilDto;
import com.example.qpiqueapp.modelo.usuarios.UsuariosResponse;
import com.example.qpiqueapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuariosViewModel extends AndroidViewModel {

    private final MutableLiveData<List<PerfilDto>> usuarios =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Integer> total =
            new MutableLiveData<>(0);

    private final MutableLiveData<Boolean> cargando =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> mensaje =
            new MutableLiveData<>();

    private final List<PerfilDto> acumulados = new ArrayList<>();

    private int page = 1;
    private final int pageSize = 6;
    private boolean ultimaPagina = false;
    private String search = "";
    private boolean inicialCargado = false;

    public UsuariosViewModel(@NonNull Application application) {
        super(application);
    }

    // Getters
    public LiveData<List<PerfilDto>> getUsuarios() { return usuarios; }
    public LiveData<Integer> getTotal() { return total; }
    public LiveData<Boolean> getCargando() { return cargando; }
    public LiveData<String> getMensaje() { return mensaje; }

    // Inicial
    public void cargarInicial() {
        if (inicialCargado) return;
        inicialCargado = true;
        resetear();
        cargarUsuarios();
    }

    // Scroll infinito
    public void cargarMas() {
        if (Boolean.TRUE.equals(cargando.getValue()) || ultimaPagina) return;
        page++;
        cargarUsuarios();
    }

    // Buscar
    public void buscar(String texto) {
        search = texto == null ? "" : texto.trim();
        resetear();
        cargarUsuarios();
    }

    // Helpers
    private void resetear() {
        page = 1;
        ultimaPagina = false;
        acumulados.clear();
        usuarios.setValue(new ArrayList<>());
    }

    public void mensajeConsumido() {
        mensaje.setValue(null);
    }

    // Llamada API
    private void cargarUsuarios() {
        cargando.setValue(true);

        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensaje.setValue("Sesión no válida");
            cargando.setValue(false);
            return;
        }

        ApiClient.getInmoServicio()
                .getUsuario("Bearer " + token, page, pageSize, search)
                .enqueue(new Callback<UsuariosResponse>() {
                    @Override
                    public void onResponse(
                            Call<UsuariosResponse> call,
                            Response<UsuariosResponse> response) {

                        cargando.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {

                            List<PerfilDto> nuevos =
                                    response.body().getUsuarios();

                            total.setValue(response.body().getTotal());

                            if (nuevos == null || nuevos.isEmpty()) {
                                ultimaPagina = true;
                                usuarios.setValue(new ArrayList<>(acumulados));
                                return;
                            }

                            acumulados.addAll(nuevos);
                            usuarios.setValue(new ArrayList<>(acumulados));

                        } else {
                            mensaje.setValue("Error al cargar usuarios");
                        }
                    }

                    @Override
                    public void onFailure(Call<UsuariosResponse> call, Throwable t) {
                        cargando.setValue(false);
                        mensaje.setValue("Error de conexión");
                    }
                });
    }
}
