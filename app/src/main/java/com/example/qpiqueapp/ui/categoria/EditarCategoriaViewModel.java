package com.example.qpiqueapp.ui.categoria;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.Categorias;
import com.example.qpiqueapp.request.ApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarCategoriaViewModel extends AndroidViewModel {

    private final MutableLiveData<Categorias> categoria = new MutableLiveData<>();
    private final MutableLiveData<Uri> imagenSeleccionada = new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<Boolean> volverAtras = new MutableLiveData<>();

    public EditarCategoriaViewModel(@NonNull Application app) {
        super(app);
    }

    // Getter

    public LiveData<Categorias> getCategoria() { return categoria; }
    public LiveData<Uri> getImagenSeleccionada() { return imagenSeleccionada; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getMensaje() { return mensaje; }
    public LiveData<Boolean> getVolverAtras() { return volverAtras; }

    // Iniciar

    public void inicializar(Categorias c) {
        if (c == null) {
            mensaje.setValue("Categoría no encontrada");
            volverAtras.setValue(true);
            return;
        }
        categoria.setValue(c);
    }

    public void setImagen(Uri uri) {
        imagenSeleccionada.setValue(uri);
    }

    // Metodos

    public void guardarCambios(Context context, String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            mensaje.setValue("Ingrese un nombre");
            return;
        }

        Categorias c = categoria.getValue();
        if (c == null) {
            mensaje.setValue("Categoría no válida");
            volverAtras.setValue(true);
            return;
        }

        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensaje.setValue("Sesión no válida");
            volverAtras.setValue(true);
            return;
        }

        loading.setValue(true);

        RequestBody idRB =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(c.getId()));
        RequestBody nombreRB =
                RequestBody.create(MediaType.parse("text/plain"), nombre);

        MultipartBody.Part imagenPart = crearImagenPart(context);

        ApiClient.getInmoServicio()
                .editarCategoria(
                        "Bearer " + token,
                        c.getId(),
                        idRB,
                        nombreRB,
                        imagenPart
                )
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loading.postValue(false);
                        if (response.isSuccessful()) {
                            mensaje.postValue("Categoría actualizada");
                            volverAtras.postValue(true);
                        } else {
                            mensaje.postValue("Error al actualizar");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loading.postValue(false);
                        mensaje.postValue("Error de conexión");
                    }
                });
    }

    // Utilidad

    private MultipartBody.Part crearImagenPart(Context context) {
        Uri uri = imagenSeleccionada.getValue();
        if (uri == null) return null;

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File file = new File(context.getCacheDir(),
                    "categoria_" + System.currentTimeMillis());

            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.close();
            inputStream.close();

            RequestBody body = RequestBody.create(
                    MediaType.parse(context.getContentResolver().getType(uri)),
                    file
            );

            return MultipartBody.Part.createFormData(
                    "imagen",
                    file.getName(),
                    body
            );

        } catch (Exception e) {
            mensaje.postValue("Error al procesar la imagen");
            return null;
        }
    }

    // Eventos

    public void mensajeConsumido() {
        mensaje.setValue(null);
    }

    public void volverConsumido() {
        volverAtras.setValue(false);
    }
}