package com.example.qpiqueapp.ui.productos;

import android.app.Application;
import android.net.Uri;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarProductoViewModel extends AndroidViewModel {

    private final MutableLiveData<Productos> producto = new MutableLiveData<>();
    private final MutableLiveData<List<Categorias>> categorias = new MutableLiveData<>();
    private final MutableLiveData<Uri> imagenSeleccionada = new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<Boolean> volverAtras = new MutableLiveData<>();

    public EditarProductoViewModel(@NonNull Application app) {
        super(app);
    }

    // Getters

    public LiveData<Productos> getProducto() { return producto; }
    public LiveData<List<Categorias>> getCategorias() { return categorias; }
    public LiveData<Uri> getImagenSeleccionada() { return imagenSeleccionada; }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getMensaje() { return mensaje; }
    public LiveData<Boolean> getVolverAtras() { return volverAtras; }

    // Init

    public void inicializar(Productos p) {
        if (p == null) {
            mensaje.setValue("Producto no encontrado");
            volverAtras.setValue(true);
            return;
        }
        producto.setValue(p);
    }

    public void setImagen(Uri uri) {
        imagenSeleccionada.setValue(uri);
    }

    // Categorias

    public void cargarCategorias() {
        ApiClient.getInmoServicio()
                .getCategorias()
                .enqueue(new Callback<List<Categorias>>() {
                    @Override
                    public void onResponse(
                            Call<List<Categorias>> call,
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

    public void guardarCambios(
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

        Productos p = producto.getValue();
        if (p == null) {
            mensaje.setValue("Producto no disponible");
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

        MultipartBody.Part imagenPart = crearImagenPart();

        ApiClient.getInmoServicio()
                .editarProducto(
                        "Bearer " + token,
                        p.getId(),
                        texto(nombre),
                        texto(descripcion),
                        texto(String.valueOf(precio)),
                        texto(String.valueOf(stock)),
                        texto(String.valueOf(categoriaId)),
                        imagenPart
                )
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(
                            Call<ResponseBody> call,
                            Response<ResponseBody> response) {

                        loading.postValue(false);

                        if (response.isSuccessful()) {
                            mensaje.postValue("Producto actualizado");
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


    private RequestBody texto(String valor) {
        return RequestBody.create(
                MediaType.parse("text/plain"),
                valor
        );
    }

    private MultipartBody.Part crearImagenPart() {
        Uri uri = imagenSeleccionada.getValue();
        if (uri == null) return null; // solo enviar si hay nueva imagen

        try (InputStream is = getApplication().getContentResolver().openInputStream(uri)) {

            File file = new File(getApplication().getCacheDir(),
                    "producto_" + System.currentTimeMillis());

            try (FileOutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
            }

            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);

            return MultipartBody.Part.createFormData("nuevaImagen", file.getName(), body);

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

