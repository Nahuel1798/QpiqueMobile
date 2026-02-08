package com.example.qpiqueapp.ui.productos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.productos.Productos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CarritoViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Productos>> carrito =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Double> total =
            new MutableLiveData<>(0.0);

    private final MutableLiveData<String> mensaje =
            new MutableLiveData<>();

    public CarritoViewModel(@NonNull Application application) {
        super(application);
    }

    // Getters
    public LiveData<List<Productos>> getCarrito() { return carrito; }
    public LiveData<Double> getTotal() { return total; }
    public LiveData<String> getMensaje() { return mensaje; }

    // Agregar producto
    public void agregarProducto(Productos producto) {
        List<Productos> lista = new ArrayList<>(carrito.getValue());

        for (Productos p : lista) {
            if (p.getId() == producto.getId()) {

                if (p.getCantidad() < p.getStock()) {
                    p.setCantidad(p.getCantidad() + 1);
                    actualizar(lista);
                } else {
                    mensaje.setValue("No hay más stock disponible");
                }
                return;
            }
        }

        producto.setCantidad(1);
        lista.add(producto);
        actualizar(lista);
    }

    // Quitar producto
    public void quitarProducto(Productos producto) {
        List<Productos> lista = new ArrayList<>(carrito.getValue());
        lista.removeIf(p -> p.getId() == producto.getId());
        actualizar(lista);
    }

    // Actualizar cantidad (desde Adapter)
    public void actualizarCantidad() {
        calcularTotal();
    }

    // Limpiar carrito
    public void limpiarCarrito() {
        carrito.setValue(new ArrayList<>());
        total.setValue(0.0);
    }

    // Cantidad de items
    public int getCantidadItems() {
        return carrito.getValue() != null ? carrito.getValue().size() : 0;
    }

    // Consumir mensaje
    public void mensajeConsumido() {
        mensaje.setValue(null);
    }

    // Helpers
    private void actualizar(List<Productos> lista) {
        Collections.sort(
                lista,
                Comparator.comparing(
                        Productos::getNombre,
                        String.CASE_INSENSITIVE_ORDER
                )
        );
        carrito.setValue(lista);
        calcularTotal();
    }

    private void calcularTotal() {
        double suma = 0;
        for (Productos p : carrito.getValue()) {
            suma += p.getPrecio() * p.getCantidad();
        }
        total.setValue(suma);
    }
}
