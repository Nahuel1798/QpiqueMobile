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

    private CarritoViewModel carritoViewModel;
    private VentasViewModel ventasViewModel;
    private CrearVentaViewModel crearVentaViewModel;

    private CarritoAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCarritoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        ventasViewModel = new ViewModelProvider(requireActivity()).get(VentasViewModel.class);
        crearVentaViewModel = new ViewModelProvider(requireActivity()).get(CrearVentaViewModel.class);

        adapter = new CarritoAdapter(
                new ArrayList<>(),
                new CarritoAdapter.OnEliminarClick() {
                    @Override
                    public void onEliminar(Productos producto) {
                        carritoViewModel.quitarProducto(producto);
                    }

                    @Override
                    public void onCantidadCambiada() {
                        carritoViewModel.calcularTotal();
                    }
                }
        );

        binding.rvCarrito.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCarrito.setAdapter(adapter);

        carritoViewModel.getCarrito().observe(getViewLifecycleOwner(), lista -> {
            adapter.setLista(lista);
            binding.layoutVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
            carritoViewModel.calcularTotal();
        });

        carritoViewModel.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                carritoViewModel.limpiarMensaje();
            }
        });

        carritoViewModel.getTotal().observe(
                getViewLifecycleOwner(),
                total -> binding.tvTotal.setText("$ " + String.format("%.2f", total))
        );

        ventasViewModel.getClienteSeleccionado().observe(getViewLifecycleOwner(), cliente -> {
            if (cliente != null) {
                binding.cardCliente.setVisibility(View.VISIBLE);
                binding.tvClienteNombre.setText(cliente.getNombre() + " " + cliente.getApellido());
                binding.tvClienteEmail.setText(cliente.getEmail());
                binding.btnSeleccionarCliente.setText("Cambiar Cliente");
            } else {
                binding.cardCliente.setVisibility(View.GONE);
                binding.btnSeleccionarCliente.setText("Seleccionar Cliente");
            }
        });

        crearVentaViewModel.getVentaCreada().observe(getViewLifecycleOwner(), creada -> {
            if (creada != null && creada) {
                carritoViewModel.limpiarCarrito();
                NavHostFragment.findNavController(this).popBackStack();
                crearVentaViewModel.resetVentaCreada();
            }
        });

        binding.btnSeleccionarCliente.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean("modoSeleccion", true);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_carritoFragment_to_nav_slideshow, args);
        });

        binding.btnQuitarCliente.setOnClickListener(v -> {
            ventasViewModel.deseleccionarCliente();
            Toast.makeText(requireContext(), "Cliente deseleccionado", Toast.LENGTH_SHORT).show();
        });

        binding.btnConfirmarCompra.setOnClickListener(v -> {
            if (carritoViewModel.getCantidadItems() == 0) return;

            if (ventasViewModel.getClienteSeleccionado().getValue() == null) {
                Toast.makeText(requireContext(), "Debe seleccionar un cliente", Toast.LENGTH_SHORT).show();
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
