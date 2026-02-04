package com.example.qpiqueapp.ui.usuarios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.PerfilDto;
import com.example.qpiqueapp.modelo.UsuarioUpdateRequest;
import com.example.qpiqueapp.request.ApiClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarUsuarioViewModel extends AndroidViewModel {

    private final MutableLiveData<PerfilDto> perfil = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<Boolean> volverAtras = new MutableLiveData<>(false);

    private UsuarioUpdateRequest usuario;

    public EditarUsuarioViewModel(@NonNull Application app) {
        super(app);
    }

    // Getter

    public LiveData<PerfilDto> getPerfil() {
        return perfil;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public LiveData<Boolean> getVolverAtras() {
        return volverAtras;
    }

    // Metodos

    public void cargarUsuarioPorId(String idUsuario) {
        loading.setValue(true);

        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        ApiClient.getInmoServicio()
                .getUsuarioPorId(auth, idUsuario)
                .enqueue(new Callback<PerfilDto>() {
                    @Override
                    public void onResponse(Call<PerfilDto> call, Response<PerfilDto> response) {
                        loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            PerfilDto p = response.body();
                            perfil.setValue(p);

                            usuario = new UsuarioUpdateRequest(
                                    p.getId(),
                                    p.getNombre(),
                                    p.getApellido(),
                                    p.getEmail(),
                                    p.getRoles(),
                                    null
                            );
                        } else {
                            mensaje.setValue("Error al cargar usuario");
                        }
                    }

                    @Override
                    public void onFailure(Call<PerfilDto> call, Throwable t) {
                        loading.setValue(false);
                        mensaje.setValue("Error de conexión");
                    }
                });
    }

    public void guardarCambios(String nombre, String apellido, String email, List<String> rol) {
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
            mensaje.setValue("Completa todos los campos");
            return;
        }

        if (usuario == null) {
            mensaje.setValue("Usuario no disponible");
            return;
        }

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setRoles(rol);
        usuario.setAvatar(null);

        loading.setValue(true);

        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        ApiClient.getInmoServicio()
                .editarUsuario(auth, usuario.getId(), usuario)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.setValue(false);

                        if (response.isSuccessful()) {
                            mensaje.setValue("Usuario actualizado");
                            volverAtras.setValue(true);
                        } else {
                            mensaje.setValue("Error al actualizar usuario");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loading.setValue(false);
                        mensaje.setValue("Error de conexión");
                    }
                });
    }

    // Consumidores

    public void mensajeConsumido() {
        mensaje.setValue(null);
    }

    public void volverConsumido() {
        volverAtras.setValue(false);
    }
}


