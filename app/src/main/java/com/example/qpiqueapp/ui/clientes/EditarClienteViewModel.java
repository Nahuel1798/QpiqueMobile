package com.example.qpiqueapp.ui.clientes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.Clientes;
import com.example.qpiqueapp.request.ApiClient;
import com.example.qpiqueapp.ui.productos.EditarProductoViewModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarClienteViewModel extends AndroidViewModel {
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

    private final MutableLiveData<Clientes> cliente = new MutableLiveData<>();
    private final MutableLiveData<UiState> estado = new MutableLiveData<>();

    public EditarClienteViewModel(@NonNull Application app) {
        super(app);
    }

    public LiveData<Clientes> getCliente() {
        return cliente;
    }

    public LiveData<UiState> getEstado() {
        return estado;
    }

    public void setCliente(Clientes c) {
        cliente.setValue(c);
    }

    public void guardarCambios(String nombre, String apellido, String telefono, String email) {
        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
            estado.setValue(UiState.error("Completa todos los campos"));
            return;
        }

        Clientes c = cliente.getValue();
        if (c == null) {
            estado.setValue(UiState.error("Cliente no disponible"));
            return;
        }
        c.setNombre(nombre);
        c.setApellido(apellido);
        c.setTelefono(telefono);
        c.setEmail(email);

        estado.setValue(UiState.loading());

        String token = ApiClient.leerToken(getApplication());
        String auth = "Bearer " + token;

        ApiClient.getInmoServicio()
                .editarCliente(auth, c.getId(), c)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            estado.postValue(UiState.success());
                        } else {
                            estado.postValue(UiState.error("Error al actualizar"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        estado.postValue(UiState.error("Error de conexión"));
                    }
                });
    }

}
