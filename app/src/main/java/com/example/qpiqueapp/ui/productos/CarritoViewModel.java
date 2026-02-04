package com.example.qpiqueapp.ui.productos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qpiqueapp.modelo.Clientes;
import com.example.qpiqueapp.modelo.Productos;

import java.util.ArrayList;
import java.util.List;

public class CarritoViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Productos>> carrito =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Double> total =
            new MutableLiveData<>(0.0);

    public CarritoViewModel(@NonNull Application application) {
        super(application);
    }

    // Getter

    public LiveData<List<Productos>> getCarrito() {
        return carrito;
    }

    public LiveData<Double> getTotal() {
        return total;
    }

    // Metodos
    public void agregarProducto(Productos producto) {
        List<Productos> lista = new ArrayList<>(carrito.getValue());

        for (Productos p : lista) {
            if (p.getId() == producto.getId()) {
                if (p.getCantidad() < p.getStock()) {
                    p.setCantidad(p.getCantidad() + 1);
                    carrito.setValue(lista);
                    calcularTotal();
                }
                return;
            }
        }

        producto.setCantidad(1);
        lista.add(producto);

        carrito.setValue(lista);
        calcularTotal();
    }
    public void quitarProducto(Productos producto) {
        List<Productos> lista =
                carrito.getValue() != null
                        ? new ArrayList<>(carrito.getValue())
                        : new ArrayList<>();

        lista.remove(producto);

        carrito.setValue(lista);
        calcularTotal();
    }

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

    public void limpiarCarrito() {
        carrito.setValue(new ArrayList<>());
        total.setValue(0.0);
    }

    public int getCantidadItems() {
        return carrito.getValue() != null ? carrito.getValue().size() : 0;
    }
}
