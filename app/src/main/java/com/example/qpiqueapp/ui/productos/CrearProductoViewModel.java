package com.example.qpiqueapp.ui.productos;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.categoria.Categorias;
import com.example.qpiqueapp.modelo.productos.Productos;
import com.example.qpiqueapp.request.ApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearProductoViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Categorias>> categorias = new MutableLiveData<>();
    private final MutableLiveData<Uri> imagenSeleccionada = new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<Boolean> volverAtras = new MutableLiveData<>();

    public CrearProductoViewModel(@NonNull Application application) {
        super(application);
    }

    // Getters
    public LiveData<List<Categorias>> getCategorias() { return categorias; }
    public LiveData<Uri> getImagenSeleccionada() { return imagenSeleccionada; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getMensaje() { return mensaje; }
    public LiveData<Boolean> getVolverAtras() { return volverAtras; }

    // Imagen
    public void setImagen(Uri uri) {
        imagenSeleccionada.setValue(uri);
    }

    // Categorías
    public void cargarCategorias() {
        ApiClient.getInmoServicio()
                .getCategorias()
                .enqueue(new Callback<List<Categorias>>() {
                    @Override
                    public void onResponse(Call<List<Categorias>> call,
                                           Response<List<Categorias>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categorias.postValue(response.body());
                        } else {
                            mensaje.postValue("Error al cargar categorías");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Categorias>> call, Throwable t) {
                        mensaje.postValue("Error de conexión");
                    }
                });
    }

    // Guardar
    public void crearProducto(
            String nombre,
            String descripcion,
            String precioStr,
            String stockStr,
            int categoriaId
    ) {

        if (nombre.isEmpty() || descripcion.isEmpty()
                || precioStr.isEmpty() || stockStr.isEmpty()) {
            mensaje.setValue("Completa todos los campos");
            return;
        }

        double precio;
        int stock;

        try {
            precio = Double.parseDouble(precioStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            mensaje.setValue("Precio o stock inválido");
            return;
        }

        if (imagenSeleccionada.getValue() == null) {
            mensaje.setValue("Selecciona una imagen");
            return;
        }

        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensaje.setValue("Sesión expirada");
            volverAtras.setValue(true);
            return;
        }

        loading.setValue(true);

        MultipartBody.Part imagenPart = crearImagenPart(imagenSeleccionada.getValue(), "producto");
        if (imagenPart == null) {
            mensaje.setValue("Error al procesar la imagen");
            return;
        }

        ApiClient.getInmoServicio()
                .crearProducto(
                        "Bearer " + token,
                        texto(nombre),
                        texto(descripcion),
                        texto(String.valueOf(precio)),
                        texto(String.valueOf(stock)),
                        texto(String.valueOf(categoriaId)),
                        imagenPart
                )
                .enqueue(new Callback<Productos>() {
                    @Override
                    public void onResponse(Call<Productos> call,
                                           Response<Productos> response) {
                        loading.postValue(false);
                        if (response.isSuccessful()) {
                            mensaje.postValue("Producto creado correctamente");
                            volverAtras.postValue(true);
                        } else {
                            mensaje.postValue("Error al crear producto");
                        }
                    }

                    @Override
                    public void onFailure(Call<Productos> call, Throwable t) {
                        loading.postValue(false);
                        mensaje.postValue("Error de conexión");
                    }
                });
    }

    // Helpers
    private RequestBody texto(String valor) {
        return RequestBody.create(MediaType.parse("text/plain"), valor);
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
    public void mensajeConsumido() {
        mensaje.setValue(null);
    }

    public void volverConsumido() {
        volverAtras.setValue(false);
    }
}
