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

import com.example.qpiqueapp.databinding.FragmentEditarVentaBinding;
import com.example.qpiqueapp.modelo.venta.DetalleVenta;
import com.example.qpiqueapp.modelo.venta.Ventas;

import java.util.ArrayList;
import java.util.List;

public class EditarVentaFragment extends Fragment {

    private FragmentEditarVentaBinding binding;
    private EditarVentaViewModel viewModel;

    private int ventaId;
    private List<DetalleVenta> detalles = new ArrayList<>();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EditarVentaViewModel.class);

        // RecyclerView
        adapter = new EditarVentaAdapter(detalles, this::recalcularTotal);
        binding.recyclerDetalles.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerDetalles.setAdapter(adapter);

        // Validar argumentos
        if (getArguments() == null || !getArguments().containsKey("ventas")) {
            Toast.makeText(getContext(), "Error al cargar la venta", Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }

        // Recibir venta
        Ventas venta = (Ventas) getArguments().getSerializable("ventas");
        if (venta == null) {
            Toast.makeText(getContext(), "Venta inválida", Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }

        // Inicializar datos
        ventaId = venta.getId();
        detalles.clear();
        detalles.addAll(venta.getDetalleVentas());
        adapter.notifyDataSetChanged();
        recalcularTotal();

        // Botón Guardar
        binding.btnGuardarVenta.setOnClickListener(v ->
                viewModel.editarVenta(ventaId, detalles)
        );

//        binding.btnAgregarProducto.setOnClickListener(v -> {
//            mostrarDialogoAgregarProducto();
//        });

        observarViewModel();
    }

    private void observarViewModel() {

        viewModel.getVentaActualizada().observe(getViewLifecycleOwner(), venta -> {
            if (venta != null) {
                Toast.makeText(getContext(),
                        "Venta actualizada. Total: $" + venta.getTotal(),
                        Toast.LENGTH_LONG).show();

                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnGuardarVenta.setEnabled(!loading);
        });
    }

    // Recalcula total en vivo
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
