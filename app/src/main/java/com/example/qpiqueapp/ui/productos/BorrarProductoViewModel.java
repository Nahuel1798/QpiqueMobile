package com.example.qpiqueapp.ui.productos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BorrarProductoViewModel extends AndroidViewModel {
    public MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> eliminado = new MutableLiveData<>(false);
    public MutableLiveData<String> mensaje = new MutableLiveData<>();

    public BorrarProductoViewModel(@NonNull Application app) {
        super(app);
    }

    public void eliminarProducto(int id) {
        loading.setValue(true);

        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        ApiClient.getInmoServicio()
                .eliminarProducto(auth, id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        loading.setValue(false);

                        if (response.isSuccessful()) {
                            eliminado.setValue(true);
                            mensaje.setValue("Producto eliminado correctamente");
                            return;
                        }

                        if (response.code() == 403) {
                            mensaje.setValue("Solo un administrador puede borrar productos");
                        } else if (response.code() == 404) {
                            mensaje.setValue("El producto no existe");
                        } else {
                            mensaje.setValue("Error al eliminar el producto");
                        }

                        eliminado.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loading.setValue(false);
                        eliminado.setValue(false);
                        mensaje.setValue("Error de conexión con el servidor");
                    }
                });
    }
}
