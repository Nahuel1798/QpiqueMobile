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

    private final MutableLiveData<List<Productos>> carrito = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> total = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();


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
                    ordenarYActualizar(lista);
                } else {
                    mensaje.setValue("No hay más stock disponible");
                }
                return;
            }
        }

        producto.setCantidad(1);
        lista.add(producto);
        ordenarYActualizar(lista);
    }

    // Quitar producto
    public void quitarProducto(Productos producto) {
        List<Productos> lista = carrito.getValue() != null ? new ArrayList<>(carrito.getValue()) : new ArrayList<>();
        lista.removeIf(p -> p.getId() == producto.getId());
        ordenarYActualizar(lista);
    }

    // Calcula total
    public void calcularTotal() {
        List<Productos> listaActual = carrito.getValue();
        double sumaTotal = 0.0;
        if (listaActual != null) {
            for (Productos p : listaActual) {
                sumaTotal += p.getPrecio() * p.getCantidad();
            }
        }
        total.setValue(sumaTotal);
    }

    // Limpiar carrito
    public void limpiarCarrito() {
        carrito.setValue(new ArrayList<>());
        total.setValue(0.0);
    }
    public void limpiarMensaje() {
        mensaje.setValue(null);
    }

    public int getCantidadItems() {
        return carrito.getValue() != null ? carrito.getValue().size() : 0;
    }

    // Ordena alfabéticamente y actualiza LiveData
    private void ordenarYActualizar(List<Productos> lista) {
        Collections.sort(lista, Comparator.comparing(Productos::getNombre, String.CASE_INSENSITIVE_ORDER));
        carrito.setValue(lista);
        calcularTotal();
    }
}
