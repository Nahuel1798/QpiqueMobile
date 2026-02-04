package com.example.qpiqueapp.ui.usuarios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.qpiqueapp.databinding.FragmentEditarUsuarioBinding;

import java.util.List;

public class EditarUsuarioFragment extends Fragment {

    private FragmentEditarUsuarioBinding binding;
    private EditarUsuarioViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEditarUsuarioBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(EditarUsuarioViewModel.class);

        // Spinner roles
        String[] roles = {"Administrador", "Empleado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                roles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spRol.setAdapter(adapter);

        String usuarioId = getArguments() != null
                ? getArguments().getString("usuarioId")
                : null;

        if (usuarioId == null) {
            Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(this).popBackStack();
            return binding.getRoot();
        }

        vm.cargarUsuarioPorId(usuarioId);

        // Observer

        vm.getPerfil().observe(getViewLifecycleOwner(), perfil -> {
            if (perfil == null) return;

            binding.etNombre.setText(perfil.getNombre());
            binding.etApellido.setText(perfil.getApellido());
            binding.etEmail.setText(perfil.getEmail());

            boolean esAdmin = perfil.getRoles().contains("Administrador");
            binding.spRol.setSelection(esAdmin ? 0 : 1);
        });

        vm.getLoading().observe(getViewLifecycleOwner(),
                l -> binding.btnGuardar.setEnabled(!Boolean.TRUE.equals(l)));

        vm.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                vm.mensajeConsumido();
            }
        });

        vm.getVolverAtras().observe(getViewLifecycleOwner(), volver -> {
            if (Boolean.TRUE.equals(volver)) {
                NavHostFragment.findNavController(this).popBackStack();
                vm.volverConsumido();
            }
        });

        // Acciones

        binding.btnGuardar.setOnClickListener(v -> {
            List<String> rol = binding.spRol.getSelectedItem().toString()
                    .equals("Administrador")
                    ? List.of("Administrador")
                    : List.of("Empleado");

            vm.guardarCambios(
                    binding.etNombre.getText().toString().trim(),
                    binding.etApellido.getText().toString().trim(),
                    binding.etEmail.getText().toString().trim(),
                    rol
            );
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
