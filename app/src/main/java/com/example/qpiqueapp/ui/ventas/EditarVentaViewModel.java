package com.example.qpiqueapp.ui.ventas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.venta.DetalleVenta;
import com.example.qpiqueapp.modelo.venta.VentaCrearRequest;
import com.example.qpiqueapp.modelo.venta.Ventas;

public class EditarVentaViewModel extends AndroidViewModel {

    public static class UiState {
        public final boolean loading;
        public final boolean success;
        public final String error;

        private UiState(boolean loading, boolean success, String error) {
            this.loading = loading;
            this.success = success;
            this.error = error;
        }

        public static EditarVentaViewModel.UiState loading() {
            return new EditarVentaViewModel.UiState(true, false, null);
        }

        public static EditarVentaViewModel.UiState success() {
            return new EditarVentaViewModel.UiState(false, true, null);
        }

        public static EditarVentaViewModel.UiState error(String msg) {
            return new EditarVentaViewModel.UiState(false, false, msg);
        }
    }
    private final MutableLiveData<Ventas> venta = new MutableLiveData<>();
    private final MutableLiveData<VentaCrearRequest> ventaCrearRequest = new MutableLiveData<>();
    private final MutableLiveData<DetalleVenta> detalleVenta = new MutableLiveData<>();
    private final MutableLiveData<UiState> estado = new MutableLiveData<>(UiState.success());
    private final MutableLiveData<Boolean> volverAtras = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    public EditarVentaViewModel(@NonNull Application app) {
        super(app);
    }

    public LiveData<Ventas> getVenta() {
        return venta;
    }
    public LiveData<VentaCrearRequest> getVentaCrearRequest() {
        return ventaCrearRequest;
    }
    public LiveData<DetalleVenta> getDetalleVenta() {
        return detalleVenta;
    }


    public LiveData<UiState> getEstado() {
        return estado;
    }

    public LiveData<Boolean> getVolverAtras() {
        return volverAtras;
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public void setVenta(Ventas v) {
        venta.setValue(v);
    }
    public void setDetalleVenta(DetalleVenta dv) {
        detalleVenta.setValue(dv);
    }


    public void editarVenta(String cliente, String producto, String dia, String fechaDesde, String fechaHasta) {
        if (cliente.isEmpty() || producto.isEmpty() || dia.isEmpty() || fechaDesde.isEmpty() || fechaHasta.isEmpty()) {
            estado.setValue(UiState.error("Completa todos los campos"));
            return;
        }

        VentaCrearRequest v = ventaCrearRequest.getValue();
        if (v == null) {
            estado.setValue(UiState.error("Venta no disponible"));
            return;
        }


    }


}
