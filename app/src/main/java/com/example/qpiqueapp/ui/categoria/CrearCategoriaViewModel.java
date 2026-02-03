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
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearCategoriaViewModel extends AndroidViewModel {
    // MutableLiveData
    private final MutableLiveData<Uri> imagenSeleccionada = new MutableLiveData<>();
    private final MutableLiveData<Boolean> abrirSelectorImagen = new MutableLiveData<>();
    private final MutableLiveData<Boolean> creado = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public CrearCategoriaViewModel(@NonNull Application application) {
        super(application);
    }

    // Getters
    public LiveData<Uri> getImagenSeleccionada() { return imagenSeleccionada; }
    public LiveData<Boolean> getAbrirSelectorImagen() { return abrirSelectorImagen; }
    public LiveData<Boolean> getCreado() { return creado; }

    public LiveData<String> getError() { return mensajeError; }

    // Metodos
    public void solicitarSeleccionImagen() {
        abrirSelectorImagen.setValue(true);
    }

    public void onImagenSeleccionada(Uri uri) {
        imagenSeleccionada.setValue(uri);
    }

    public void crearCategoria(Context context, String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            mensajeError.setValue("Ingrese un nombre");
            return;
        }

        Uri imagenUri = imagenSeleccionada.getValue();
        if (imagenUri == null) {
            mensajeError.setValue("Seleccione una imagen");
            return;
        }

        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensajeError.setValue("No hay sesión activa");
            return;
        }

        MultipartBody.Part imagenPart;
        try (InputStream inputStream = context.getContentResolver().openInputStream(imagenUri)) {

            File tempFile = new File(context.getCacheDir(),
                    "categoria_" + System.currentTimeMillis() + ".jpg");

            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            }

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(context.getContentResolver().getType(imagenUri)),
                    tempFile
            );

            imagenPart = MultipartBody.Part.createFormData(
                    "Imagen",
                    tempFile.getName(),
                    requestFile
            );

        } catch (Exception e) {
            mensajeError.setValue("Error al procesar la imagen");
            return;
        }

        RequestBody nombreBody =
                RequestBody.create(MediaType.parse("text/plain"), nombre);

        ApiClient.getInmoServicio()
                .crearCategoria("Bearer " + token, nombreBody, imagenPart)
                .enqueue(new Callback<Categorias>() {
                    @Override
                    public void onResponse(Call<Categorias> call, Response<Categorias> response) {
                        if (response.isSuccessful()) {
                            creado.setValue(true);
                        } else {
                            mensajeError.setValue("Error al crear categoría (" + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(Call<Categorias> call, Throwable t) {
                        mensajeError.setValue(t.getMessage());
                    }
                });
    }

    // Eventos
    public void eventoImagenConsumido() {
        abrirSelectorImagen.setValue(false);
    }

    public void errorMostrado() {
        mensajeError.setValue(null);
    }

    public void creadoConsumido() {
        creado.setValue(false);
    }
}
