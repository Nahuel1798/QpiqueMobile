package com.example.qpiqueapp.ui.productos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qpiqueapp.databinding.FragmentBorrarProductoBinding;
import com.example.qpiqueapp.modelo.Productos;


public class BorrarProductoFragment extends Fragment {
    private FragmentBorrarProductoBinding binding;
    private BorrarProductoViewModel vm;
    private Productos producto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBorrarProductoBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(BorrarProductoViewModel.class);
        producto = (Productos) getArguments().getSerializable("producto");
        binding.tvMensaje.setText("¿Desea eliminar el producto " + producto.getNombre() + "?");
        binding.btnConfirmar.setOnClickListener(v -> {
            vm.eliminarProducto(producto.getId());
        });
        binding.btnCancelar.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
        vm.getEliminado().observe(getViewLifecycleOwner(), ok -> {
            if (ok){
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