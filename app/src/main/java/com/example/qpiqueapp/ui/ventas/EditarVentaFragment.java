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
import com.example.qpiqueapp.databinding.FragmentEditarVentaBinding;
import com.example.qpiqueapp.modelo.productos.Productos;
import com.example.qpiqueapp.modelo.venta.DetalleVenta;
import com.example.qpiqueapp.modelo.venta.Ventas;

import java.util.ArrayList;
import java.util.List;

public class EditarVentaFragment extends Fragment {

    private FragmentEditarVentaBinding binding;
    private EditarVentaViewModel viewModel;

    private int ventaId = 0;
    private final List<DetalleVenta> detalles = new ArrayList<>();
    private EditarVentaAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEditarVentaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel compartido
        viewModel = new ViewModelProvider(requireActivity())
                .get(EditarVentaViewModel.class);

        // Recycler
        adapter = new EditarVentaAdapter(
                detalles,
                new EditarVentaAdapter.OnDetalleChangeListener() {
                    @Override
                    public void onCantidadChange() {
                        recalcularTotal();
                    }

                    @Override
                    public void onEliminar(DetalleVenta detalle) {
                        viewModel.eliminarDetalle(detalle);
                    }
                }
        );

        binding.recyclerDetalles.setLayoutManager(
                new LinearLayoutManager(getContext())
        );
        binding.recyclerDetalles.setAdapter(adapter);

        observarViewModel();

        Bundle args = getArguments();
        if (args == null) {
            Toast.makeText(getContext(),
                    "Error al cargar datos",
                    Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }

        // Cargar venta existente
        if (args.containsKey("ventas")) {

            Ventas venta = (Ventas) args.getSerializable("ventas");

            if (venta != null) {
                ventaId = venta.getId();

                viewModel.setVentaId(ventaId);

                for (DetalleVenta d : venta.getDetalleVentas()) {
                    d.setCantidadOriginal(d.getCantidad());
                }

                viewModel.setDetalles(venta.getDetalleVentas());
            }
        }

        // Volver del selector
        if (args.containsKey("productos")) {

            ArrayList<Productos> productos =
                    (ArrayList<Productos>) args.getSerializable("productos");

            if (productos != null) {
                viewModel.agregarProductos(productos);
            }
        }

        // Acciones
        binding.btnAgregarProducto.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(
                                R.id.action_editarVentaFragment_to_seleccionarVPFragment
                        )
        );

        binding.btnGuardarVenta.setOnClickListener(v -> {
            viewModel.editarVenta(viewModel.getVentaId());
            viewModel.limpiar();
            NavHostFragment.findNavController(this).popBackStack(R.id.nav_reflow, false);
        });
    }

    // Observers
    private void observarViewModel() {

        viewModel.getDetalles().observe(getViewLifecycleOwner(), lista -> {
            adapter.actualizarLista(lista);
            recalcularTotal();
        });

        viewModel.getVentaActualizada().observe(getViewLifecycleOwner(), venta -> {
            if (venta != null) {
                NavHostFragment.findNavController(this).popBackStack(R.id.editarVentaFragment, false);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.progressBar.setVisibility(
                    loading ? View.VISIBLE : View.GONE
            );
            binding.btnGuardarVenta.setEnabled(!loading);
        });
    }

    private void recalcularTotal() {
        double total = 0;
        for (DetalleVenta d : detalles) {
            total += d.getCantidad() * d.getPrecioUnitario();
        }
        binding.txtTotal.setText("$ " + total);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

