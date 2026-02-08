package com.example.qpiqueapp.ui.clientes;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.clientes.Clientes;
import com.example.qpiqueapp.request.ApiClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarClienteViewModel extends AndroidViewModel {

    private final MutableLiveData<Clientes> cliente = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navegarAtras = new MutableLiveData<>();

    public EditarClienteViewModel(@NonNull Application app) {
        super(app);
    }

    // Getters

    public LiveData<Clientes> getCliente() {
        return cliente;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public LiveData<Boolean> getNavegarAtras() {
        return navegarAtras;
    }

    public void inicializar(Clientes c) {
        if (c == null) {
            mensaje.setValue("Cliente no encontrado");
            navegarAtras.setValue(true);
            return;
        }
        cliente.setValue(c);
    }

    // Acciones

    public void guardarCambios(
            String nombre,
            String apellido,
            String telefono,
            String email
    ) {

        if (nombre.isEmpty() || apellido.isEmpty()) {
            mensaje.setValue("Completa nombre y apellido");
            return;
        }

        Clientes c = cliente.getValue();
        if (c == null) {
            mensaje.setValue("Cliente no disponible");
            navegarAtras.setValue(true);
            return;
        }

        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensaje.setValue("Sesión no válida");
            navegarAtras.setValue(true);
            return;
        }

        c.setNombre(nombre);
        c.setApellido(apellido);
        c.setTelefono(telefono);
        c.setEmail(email);

        loading.setValue(true);

        ApiClient.getInmoServicio()
                .editarCliente("Bearer " + token, c.getId(), c)
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(
                            Call<ResponseBody> call,
                            Response<ResponseBody> response) {

                        loading.setValue(false);

                        if (response.isSuccessful()) {
                            mensaje.setValue("Cliente actualizado");
                            navegarAtras.setValue(true);
                        } else {
                            mensaje.setValue("Error al actualizar (" + response.code() + ")");
                            Log.e("EDITAR_CLIENTE", "Código: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loading.setValue(false);
                        mensaje.setValue("Error de conexión");
                    }
                });
    }
}


