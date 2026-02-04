package com.example.qpiqueapp.ui.clientes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.Clientes;
import com.example.qpiqueapp.request.ApiClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarClienteViewModel extends AndroidViewModel {

    private final MutableLiveData<Clientes> cliente = new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<Boolean> volverAtras = new MutableLiveData<>();

    public EditarClienteViewModel(@NonNull Application app) {
        super(app);
    }

    // Getter

    public LiveData<Clientes> getCliente() { return cliente; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getMensaje() { return mensaje; }
    public LiveData<Boolean> getVolverAtras() { return volverAtras; }

    // Inicializar

    public void inicializar(Clientes c) {
        if (c == null) {
            mensaje.setValue("Cliente no encontrado");
            volverAtras.setValue(true);
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

        if (nombre.isEmpty() || apellido.isEmpty() ||
                telefono.isEmpty() || email.isEmpty()) {
            mensaje.setValue("Completa todos los campos");
            return;
        }

        Clientes c = cliente.getValue();
        if (c == null) {
            mensaje.setValue("Cliente no disponible");
            volverAtras.setValue(true);
            return;
        }

        c.setNombre(nombre);
        c.setApellido(apellido);
        c.setTelefono(telefono);
        c.setEmail(email);

        String token = ApiClient.leerToken(getApplication());
        if (token == null || token.isEmpty()) {
            mensaje.setValue("Sesión no válida");
            volverAtras.setValue(true);
            return;
        }

        loading.setValue(true);

        ApiClient.getInmoServicio()
                .editarCliente("Bearer " + token, c.getId(), c)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(
                            Call<ResponseBody> call,
                            Response<ResponseBody> response) {

                        loading.postValue(false);

                        if (response.isSuccessful()) {
                            mensaje.postValue("Cliente actualizado");
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

    // Eventos

    public void mensajeConsumido() {
        mensaje.setValue(null);
    }

    public void volverConsumido() {
        volverAtras.setValue(false);
    }
}

