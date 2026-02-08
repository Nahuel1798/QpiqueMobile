package com.example.qpiqueapp.ui.ventas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qpiqueapp.databinding.FragmentBorrarVentaBinding;
import com.example.qpiqueapp.modelo.venta.Ventas;

public class BorrarVentaFragment extends Fragment {

    private FragmentBorrarVentaBinding binding;
    private BorrarVentasViewModel vm;
    private Ventas venta;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBorrarVentaBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(BorrarVentasViewModel.class);

        // Recibir argumento
        if (getArguments() != null) {
            venta = (Ventas) getArguments().getSerializable("ventas");
        }

        if (venta == null) {
            NavHostFragment.findNavController(this).popBackStack();
            return binding.getRoot();
        }

        // Mostrar mensaje
        binding.tvMensaje.setText("¿Desea eliminar la venta " + venta.getId() + "?");

        // Confirmar borrado
        binding.btnConfirmar.setOnClickListener(v -> vm.eliminarVenta(venta.getId()));

        // Cancelar
        binding.btnCancelar.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack()
        );

        vm.getEliminado().observe(getViewLifecycleOwner(), ok -> {
            if (ok) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}