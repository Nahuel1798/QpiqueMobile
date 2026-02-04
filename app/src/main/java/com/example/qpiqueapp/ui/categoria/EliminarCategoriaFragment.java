package com.example.qpiqueapp.ui.categoria;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.qpiqueapp.databinding.FragmentEliminarCategoriaBinding;
import com.example.qpiqueapp.modelo.categoria.Categorias;


public class EliminarCategoriaFragment extends Fragment {
    private FragmentEliminarCategoriaBinding binding;
    private EliminarCategoriaViewModel vm;
    private Categorias categoria;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEliminarCategoriaBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(EliminarCategoriaViewModel.class);
        categoria = (Categorias) getArguments().getSerializable("categoria");
        binding.tvMensaje.setText("¿Desea eliminar la categoría " + categoria.getNombre() + "?");
        binding.btnConfirmar.setOnClickListener(v -> {
            vm.eliminarCategoria(categoria.getId());
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