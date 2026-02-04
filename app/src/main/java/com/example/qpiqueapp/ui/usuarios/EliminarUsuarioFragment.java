package com.example.qpiqueapp.ui.usuarios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.qpiqueapp.databinding.FragmentEliminarUsuarioBinding;
import com.example.qpiqueapp.modelo.perfil.PerfilDto;


public class EliminarUsuarioFragment extends Fragment {
    private FragmentEliminarUsuarioBinding binding;
    private EliminarUsuarioViewModel vm;
    private PerfilDto usuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEliminarUsuarioBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(EliminarUsuarioViewModel.class);
        usuario = (PerfilDto) getArguments().getSerializable("usuario");
        binding.tvMensaje.setText("¿Desea eliminar el usuario " + usuario.getNombre() + " " + usuario.getApellido() + "?");
        binding.btnConfirmar.setOnClickListener(v -> {
            vm.eliminarUsuario(usuario.getId());
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