package com.example.qpiqueapp.ui.perfil;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.perfil.PerfilDto;
import com.example.qpiqueapp.request.ApiClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarViewModel extends AndroidViewModel {
    private final MutableLiveData<PerfilDto> perfilLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    public EditarViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<PerfilDto> getPerfilLiveData() {
        return perfilLiveData;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<String> getMensaje() {
        return mensaje;
    }

    // Cargar perfil desde la API
    public void cargarPerfil() {
        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        ApiClient.getInmoServicio()
                .getProfile(auth)
                .enqueue(new Callback<PerfilDto>() {
                    @Override
                    public void onResponse(Call<PerfilDto> call, Response<PerfilDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            perfilLiveData.setValue(response.body());
                            System.out.println("Perfil cargado: " + response.body().getNombre());
                            System.out.println("Avatar URL: " + response.body().getAvatar());
                        } else {
                            mensaje.setValue("Error al cargar el perfil: " + response.code());
                            System.out.println("Error al cargar perfil: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<PerfilDto> call, Throwable t) {
                        mensaje.setValue("Error de conexión: " + t.getMessage());
                        t.printStackTrace();
                    }
                });
    }

    public void actualizarPerfil(String nombre, String apellido, String email, Uri avatarUri) {
        loading.setValue(true);
        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
            loading.setValue(false);
            mensaje.setValue("Todos los campos son obligatorios");
            return;
        }

        // Campos de texto
        RequestBody nombreBody = RequestBody.create(MediaType.parse("text/plain"), nombre);
        RequestBody apellidoBody = RequestBody.create(MediaType.parse("text/plain"), apellido);
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);

        MultipartBody.Part avatarPart = null;

        if (avatarUri != null) {
            try {
                InputStream inputStream =
                        getApplication().getContentResolver().openInputStream(avatarUri);

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[4096];
                int nRead;

                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                byte[] bytes = buffer.toByteArray();
                inputStream.close();

                RequestBody fileBody =
                        RequestBody.create(MediaType.parse("image/*"), bytes);

                avatarPart = MultipartBody.Part.createFormData(
                        "AvatarFile",
                        "avatar.jpg",
                        fileBody
                );

            } catch (Exception e) {
                e.printStackTrace();
                loading.setValue(false);
                mensaje.setValue("Error al leer la imagen");
                return;
            }
        }

        // Llamada a la API
        ApiClient.getInmoServicio()
                .updateMyProfile(auth, nombreBody, apellidoBody, emailBody, avatarPart)
                .enqueue(new Callback<PerfilDto>() {
                    @Override
                    public void onResponse(Call<PerfilDto> call, Response<PerfilDto> response) {
                        loading.setValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            perfilLiveData.setValue(response.body());
                            mensaje.setValue("Perfil actualizado correctamente");
                        } else {
                            try {
                                String error = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                                mensaje.setValue("Error al actualizar perfil: " + error);
                            } catch (Exception e) {
                                mensaje.setValue("Error al actualizar perfil");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PerfilDto> call, Throwable t) {
                        loading.setValue(false);
                        mensaje.setValue("Error de conexión: " + t.getMessage());
                    }
                });
    }
}
