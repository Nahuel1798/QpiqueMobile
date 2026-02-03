package com.example.qpiqueapp.ui.productos;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.Categorias;
import com.example.qpiqueapp.modelo.Productos;
import com.example.qpiqueapp.request.ApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarProductoViewModel extends AndroidViewModel {

    /* ===================== UI STATE ===================== */

    public static class UiState {
        public final boolean loading;
        public final boolean success;
        public final String error;

        private UiState(boolean loading, boolean success, String error) {
            this.loading = loading;
            this.success = success;
            this.error = error;
        }

        public static UiState loading() {
            return new UiState(true, false, null);
        }

        public static UiState success() {
            return new UiState(false, true, null);
        }

        public static UiState error(String msg) {
            return new UiState(false, false, msg);
        }
    }

    /* ===================== LIVE DATA ===================== */

    private final MutableLiveData<Productos> producto = new MutableLiveData<>();
    private final MutableLiveData<UiState> estado = new MutableLiveData<>();
    private final MutableLiveData<List<Categorias>> categoria = new MutableLiveData<>();
    private final MutableLiveData<Uri> imagenSeleccionada = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<Boolean> volverAtras = new MutableLiveData<>();



    public EditarProductoViewModel(@NonNull Application app) {
        super(app);
    }

    /* ===================== EXPOSICIÓN ===================== */

    public LiveData<Productos> getProducto() {
        return producto;
    }

    public LiveData<UiState> getEstado() {
        return estado;
    }
    public LiveData<String> getMensaje() {
        return mensaje;
    }
    public LiveData<Boolean> getVolverAtras() {
        return volverAtras;
    }

    public LiveData<Uri> getImagenSeleccionada() {
        return imagenSeleccionada;
    }

    public void setProducto(Productos p) {
        producto.setValue(p);
    }
    public LiveData<List<Categorias>> getCategoria() {return categoria;}

    public void setImagenSeleccionada(Uri uri) {
        imagenSeleccionada.setValue(uri);
    }

    /* ===================== HELPERS ===================== */

    private RequestBody crearTexto(String valor) {
        return RequestBody.create(
                MediaType.parse("text/plain"),
                valor
        );
    }

    private MultipartBody.Part crearImagenPart(Uri uri) {
        try {
            InputStream inputStream =
                    getApplication().getContentResolver().openInputStream(uri);

            File file = new File(getApplication().getCacheDir(), "producto.jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.close();
            inputStream.close();

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/*"), file);

            return MultipartBody.Part.createFormData(
                    "nuevaImagen", // ⚠️ debe coincidir con el backend
                    file.getName(),
                    requestFile
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* ===================== GUARDAR CAMBIOS ===================== */

    public void cargarCategorias(){
        ApiClient.getInmoServicio()
                .getCategorias()
                .enqueue(new Callback<List<Categorias>>() {
                    @Override
                    public void onResponse(Call<List<Categorias>> call,
                                           Response<List<Categorias>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categoria.postValue(response.body());
                        } else {
                            String error = "Error al cargar categorías";
                            estado.postValue(UiState.error(error));
                            Log.e("ERROR API", error);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Categorias>> call, Throwable t) {
                        String error = "Error de conexión";
                        estado.postValue(UiState.error(error));
                        Log.e("ERROR API", error);
                    }
                });
    }

    public void guardarCambios(
            String nombre,
            String descripcion,
            String precioStr,
            String stockStr,
            int categoriasStr
    ) {

        if (nombre.isEmpty() || descripcion.isEmpty() ||
                precioStr.isEmpty() || stockStr.isEmpty()) {
            estado.setValue(UiState.error("Completa todos los campos"));
            return;
        }


        double precio;
        int stock;

        try {
            precio = Double.parseDouble(precioStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            estado.setValue(UiState.error("Precio o stock inválido"));
            return;
        }

        Productos p = producto.getValue();
        if (p == null) {
            estado.setValue(UiState.error("Producto no disponible"));
            return;
        }

        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            estado.setValue(UiState.error("Sesión no válida"));
            return;
        }

        estado.setValue(UiState.loading());

        RequestBody nombreRB = crearTexto(nombre);
        RequestBody descripcionRB = crearTexto(descripcion);
        RequestBody precioRB = crearTexto(String.valueOf(precio));
        RequestBody stockRB = crearTexto(String.valueOf(stock));
        RequestBody categoriaIdRB = crearTexto(String.valueOf(categoriasStr));

        MultipartBody.Part imagenPart = null;
        if (imagenSeleccionada.getValue() != null) {
            imagenPart = crearImagenPart(imagenSeleccionada.getValue());
        }
        ApiClient.getInmoServicio()
                .editarProducto(
                        "Bearer " + token,
                        p.getId(),
                        nombreRB,
                        descripcionRB,
                        precioRB,
                        stockRB,
                        categoriaIdRB,
                        imagenPart
                )
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            estado.postValue(UiState.success());
                            mensaje.postValue("Producto actualizado");
                            volverAtras.postValue(true);
                        } else {
                            estado.postValue(UiState.error("Error al actualizar"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        estado.postValue(
                                UiState.error("Error de conexión")
                        );
                    }
                });
    }
}
