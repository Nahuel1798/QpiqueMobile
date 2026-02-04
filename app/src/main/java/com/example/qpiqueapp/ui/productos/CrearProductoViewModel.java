package com.example.qpiqueapp.ui.productos;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.Categorias;
import com.example.qpiqueapp.modelo.Productos;
import com.example.qpiqueapp.request.ApiClient;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class CrearProductoViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> exito = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<Categorias>> categoria = new MutableLiveData<>();

    public CrearProductoViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<Boolean> getExito() { return exito; }

    public LiveData<String> getError() { return error; }

    public LiveData<List<Categorias>> getCategoria() {return categoria;}
    public void cargarCategorias() {
        ApiClient.getInmoServicio()
                .getCategorias()
                .enqueue(new Callback<List<Categorias>>() {
                    @Override
                    public void onResponse(Call<List<Categorias>> call,
                                           Response<List<Categorias>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categoria.postValue(response.body());
                        } else {
                            error.postValue("Error al cargar categorías");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Categorias>> call, Throwable t) {
                        error.postValue("Error de conexión");
                    }
                });
    }
    public void crearProducto(String nombre, String descripcion,
                              String precio, String stock, String categoriaId, MultipartBody.Part imagen) {

        if (nombre.isEmpty() || descripcion.isEmpty() ||
                precio.isEmpty() || stock.isEmpty() || categoriaId.isEmpty()) {
            error.postValue("Todos los campos son obligatorios");
            return;
        }

        double precioDouble;
        int stockInt;
        int categoriaInt;

        try {
            precioDouble = Double.parseDouble(precio);
            stockInt = Integer.parseInt(stock);
            categoriaInt = Integer.parseInt(categoriaId);
        } catch (NumberFormatException e) {
            error.postValue("Precio, stock o categoría inválidos");
            return;
        }

        String token = ApiClient.leerToken(getApplication());
        if (token == null) {
            error.postValue("Sesión expirada");
            return;
        }
        RequestBody nombreBody = RequestBody.create(MediaType.parse("text/plain"), nombre);
        RequestBody descripcionBody = RequestBody.create(MediaType.parse("text/plain"), descripcion);
        RequestBody precioBody = RequestBody.create(MediaType.parse("text/plain"), precio);
        RequestBody stockBody = RequestBody.create(MediaType.parse("text/plain"), stock);
        RequestBody categoriaIdBody = RequestBody.create(MediaType.parse("text/plain"), categoriaId);

        ApiClient.getInmoServicio()
                .crearProducto(
                        "Bearer " + token,
                        nombreBody,
                        descripcionBody,
                        precioBody,
                        stockBody,
                        categoriaIdBody,
                        imagen
                )
                .enqueue(new Callback<Productos>() {
                    @Override
                    public void onResponse(Call<Productos> call, Response<Productos> response) {
                        if (response.isSuccessful()) {
                            exito.postValue(true);
                        } else {
                            error.postValue("Error al crear producto");
                        }
                    }
                    @Override
                    public void onFailure(Call<Productos> call, Throwable t) {
                        error.postValue(t.getMessage());
                    }
                });
    }

}
