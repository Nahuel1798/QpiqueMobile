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

    public static class UiState {
        public final boolean loading;
        public final boolean success;
        public final String error;

        private UiState(boolean loading, boolean success, String error) {
            this.loading = loading;
            this.success = success;
            this.error = error;
        }

        public static UiState loading() { return new UiState(true, false, null); }
        public static UiState success() { return new UiState(false, true, null); }
        public static UiState error(String msg) { return new UiState(false, false, msg); }
    }

    private final MutableLiveData<UsuarioUpdateRequest> usuario = new MutableLiveData<>();
    private final MutableLiveData<PerfilDto> perfilLiveData = new MutableLiveData<>();
    private final MutableLiveData<UiState> estado = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    public EditarUsuarioViewModel(@NonNull Application app) { super(app); }

    public LiveData<UsuarioUpdateRequest> getUsuario() { return usuario; }
    public LiveData<PerfilDto> getPerfilLiveData() { return perfilLiveData; }
    public LiveData<UiState> getEstado() { return estado; }
    public LiveData<String> getMensaje() { return mensaje; }

    public void setUsuario(UsuarioUpdateRequest u) { usuario.setValue(u); }

    // ========================================
    // Metodo público para cargar un usuario por ID
    // ========================================
    public void cargarUsuarioPorId(String idUsuario) {
        estado.setValue(UiState.loading());

        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        ApiClient.getInmoServicio()
                .getUsuarioPorId(auth, idUsuario)
                .enqueue(new Callback<PerfilDto>() {
                    @Override
                    public void onResponse(Call<PerfilDto> call, Response<PerfilDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PerfilDto perfil = response.body();
                            perfilLiveData.postValue(perfil);

                            UsuarioUpdateRequest u = new UsuarioUpdateRequest(
                                    perfil.getId(),
                                    perfil.getNombre(),
                                    perfil.getApellido(),
                                    perfil.getEmail(),
                                    perfil.getRoles(), // lo mantenemos solo para mostrar, no se editará
                                    null // avatar no lo usamos
                            );
                            usuario.postValue(u);

                            estado.postValue(UiState.success());
                        } else {
                            estado.postValue(UiState.error("Error al cargar usuario: " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<PerfilDto> call, Throwable t) {
                        estado.postValue(UiState.error("Error de conexión: " + t.getMessage()));
                    }
                });
    }

    // ========================================
    // Guardar cambios
    // ========================================
    public void guardarCambios(String nombre, String apellido, String email, List<String> rol) {
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || rol.isEmpty()) {
            estado.setValue(UiState.error("Completa todos los campos"));
            return;
        }

        UsuarioUpdateRequest u = usuario.getValue();
        if (u == null) {
            estado.setValue(UiState.error("Usuario no disponible"));
            return;
        }

        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(email);
        u.setRoles(rol);  // actualizamos rol
        u.setAvatar(null); // no tocamos avatar

        estado.setValue(UiState.loading());

        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        ApiClient.getInmoServicio()
                .editarUsuario(auth, u.getId(), u)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            estado.postValue(UiState.success());
                        } else {
                            estado.postValue(UiState.error("Error al actualizar usuario"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        estado.postValue(UiState.error("Error de conexión: " + t.getMessage()));
                    }
                });
    }
}

