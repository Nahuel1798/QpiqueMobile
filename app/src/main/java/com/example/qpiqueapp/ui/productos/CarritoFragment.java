package com.example.qpiqueapp.ui.productos;

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
import com.example.qpiqueapp.databinding.FragmentCarritoBinding;
import com.example.qpiqueapp.modelo.productos.Productos;
import com.example.qpiqueapp.ui.ventas.CrearVentaViewModel;
import com.example.qpiqueapp.ui.ventas.VentasViewModel;

import java.util.ArrayList;

public class CarritoFragment extends Fragment {

    private FragmentCarritoBinding binding;

    private CarritoViewModel carritoVM;
    private VentasViewModel ventasVM;
    private CrearVentaViewModel crearVentaVM;

    private CarritoAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentCarritoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        carritoVM = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        ventasVM = new ViewModelProvider(requireActivity()).get(VentasViewModel.class);
        crearVentaVM = new ViewModelProvider(requireActivity()).get(CrearVentaViewModel.class);

        adapter = new CarritoAdapter(
                new ArrayList<>(),
                new CarritoAdapter.OnEliminarClick() {
                    @Override
                    public void onEliminar(Productos producto) {
                        carritoVM.quitarProducto(producto);
                    }

                    @Override
                    public void onCantidadCambiada() {
                        carritoVM.actualizarCantidad();
                    }
                }
        );

        binding.rvCarrito.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCarrito.setAdapter(adapter);

        // Carrito
        carritoVM.getCarrito().observe(getViewLifecycleOwner(), lista -> {
            adapter.setLista(lista);
            binding.layoutVacio.setVisibility(
                    lista.isEmpty() ? View.VISIBLE : View.GONE
            );
        });

        // Total
        carritoVM.getTotal().observe(
                getViewLifecycleOwner(),
                total -> binding.tvTotal.setText("$ " + String.format("%.2f", total))
        );

        // Mensajes
        carritoVM.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                carritoVM.mensajeConsumido();
            }
        });

        // Cliente seleccionado
        ventasVM.getClienteSeleccionado().observe(getViewLifecycleOwner(), cliente -> {
            if (cliente != null) {
                binding.cardCliente.setVisibility(View.VISIBLE);
                binding.tvClienteNombre.setText(
                        cliente.getNombre() + " " + cliente.getApellido()
                );
                binding.tvClienteEmail.setText(cliente.getEmail());
                binding.btnSeleccionarCliente.setText("Cambiar Cliente");
            } else {
                binding.cardCliente.setVisibility(View.GONE);
                binding.btnSeleccionarCliente.setText("Seleccionar Cliente");
            }
        });

        // Venta creada
        crearVentaVM.getVentaCreada().observe(getViewLifecycleOwner(), creada -> {
            if (Boolean.TRUE.equals(creada)) {
                carritoVM.limpiarCarrito();
                NavHostFragment.findNavController(this).popBackStack();
                crearVentaVM.resetVentaCreada();
            }
        });

        // Botones
        binding.btnSeleccionarCliente.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean("modoSeleccion", true);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_carritoFragment_to_nav_slideshow, args);
        });

        binding.btnQuitarCliente.setOnClickListener(v -> {
            ventasVM.deseleccionarCliente();
            Toast.makeText(
                    requireContext(),
                    "Cliente deseleccionado",
                    Toast.LENGTH_SHORT
            ).show();
        });

        binding.btnConfirmarCompra.setOnClickListener(v -> {
            if (carritoVM.getCantidadItems() == 0) return;

            if (ventasVM.getClienteSeleccionado().getValue() == null) {
                Toast.makeText(
                        requireContext(),
                        "Debe seleccionar un cliente",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_carritoFragment_to_crearVentaFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
