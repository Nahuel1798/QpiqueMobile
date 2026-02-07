package com.example.qpiqueapp.ui.categoria;

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

        // Loading
        vm.loading.observe(getViewLifecycleOwner(), loading -> {
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnConfirmar.setEnabled(!loading);
        });

        // Mensajes
        vm.mensaje.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        // Navegación
        vm.eliminado.observe(getViewLifecycleOwner(), ok -> {
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