package com.example.qpiqueapp.ui.categoria;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.categoria.Categorias;
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

    public void crearCategoria(String nombre) {

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

        MultipartBody.Part imagenPart = crearImagenPart(imagenSeleccionada.getValue(), "categoria");
        if (imagenPart == null) {
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

    private MultipartBody.Part crearImagenPart(Uri uri, String prefijo) {
        if (uri == null) return null;

        try (InputStream is = getApplication().getContentResolver().openInputStream(uri)) {

            File file = new File(getApplication().getCacheDir(),
                    prefijo + "_" + System.currentTimeMillis() + ".jpg");

            try (FileOutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
            }

            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);

            return MultipartBody.Part.createFormData(
                    "Imagen",
                    file.getName(),
                    body
            );

        } catch (Exception e) {
            Log.e("IMAGEN_PART", "Error al procesar imagen", e);
            return null;
        }
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
