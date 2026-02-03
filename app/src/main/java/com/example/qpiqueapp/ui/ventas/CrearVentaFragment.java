package com.example.qpiqueapp.ui.ventas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentCrearVentaBinding;
import com.example.qpiqueapp.ui.productos.CarritoAdapter;
import com.example.qpiqueapp.ui.productos.CarritoViewModel;

import java.util.ArrayList;

public class CrearVentaFragment extends Fragment {

    private FragmentCrearVentaBinding binding;
    private CarritoViewModel carritoViewModel;
    private CrearVentaViewModel crearVentaViewModel;
    private VentasViewModel ventasViewModel;
    private CarritoAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentCrearVentaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModels compartidos
        carritoViewModel = new ViewModelProvider(requireActivity())
                .get(CarritoViewModel.class);

        crearVentaViewModel = new ViewModelProvider(requireActivity())
                .get(CrearVentaViewModel.class);

        // Recycler productos (solo lectura)
        adapter = new CarritoAdapter(
                new ArrayList<>(),
                null
        );

        binding.rvProductosVenta.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.rvProductosVenta.setAdapter(adapter);

        // Observa carrito
        carritoViewModel.getCarrito().observe(
                getViewLifecycleOwner(),
                lista -> adapter.setLista(lista)
        );

        // Observa total
        carritoViewModel.getTotal().observe(
                getViewLifecycleOwner(),
                total -> binding.tvTotal.setText(
                        "$ " + String.format("%.2f", total)
                )
        );

        ventasViewModel = new ViewModelProvider(requireActivity())
                .get(VentasViewModel.class);


        // Observa cliente seleccionado ✅
        ventasViewModel.getClienteSeleccionado().observe(
                getViewLifecycleOwner(),
                cliente -> {
                    if (cliente != null) {
                        // Muestra el nombre y deshabilita la edición
                        binding.actCliente.setText(
                                cliente.getNombre() + " " + cliente.getApellido()
                        );
                        binding.actCliente.setEnabled(false); // Evita que el usuario edite el campo
                        crearVentaViewModel.setCliente(cliente);
                    } else {
                        // Si no hay cliente, muestra un mensaje y permite la selección (si aplica)
                        binding.actCliente.setText("Sin cliente seleccionado");
                        binding.actCliente.setEnabled(true); // O false si siempre se debe seleccionar antes
                    }
                }
        );

        // Botón confirmar venta
        binding.btnCrearVenta.setOnClickListener(v -> {
            crearVentaViewModel.crearVenta(
                    carritoViewModel.getCarrito().getValue()
            );
        });

        // Estados
        crearVentaViewModel.getVentaCreada().observe(
                getViewLifecycleOwner(),
                creada -> {
                    if (creada) {
                        Toast.makeText(
                                requireContext(),
                                "Venta creada con éxito",
                                Toast.LENGTH_SHORT
                        ).show();

                        carritoViewModel.limpiarCarrito();
                        // Navegar a la pantalla de detalles de la venta en lugar de volver atrás
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.action_crearVentaFragment_to_nav_reflow);
                    }
                }
        );

        crearVentaViewModel.getError().observe(
                getViewLifecycleOwner(),
                error -> {
                    if (error != null) {
                        Toast.makeText(
                                requireContext(),
                                error,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
