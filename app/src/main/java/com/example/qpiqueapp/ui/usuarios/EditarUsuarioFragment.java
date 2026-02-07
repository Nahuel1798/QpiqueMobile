package com.example.qpiqueapp.ui.usuarios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.qpiqueapp.databinding.FragmentEditarUsuarioBinding;

public class EditarUsuarioFragment extends Fragment {

    private FragmentEditarUsuarioBinding binding;
    private EditarUsuarioViewModel vm;
    private String usuarioId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEditarUsuarioBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(EditarUsuarioViewModel.class);

        // Spinner roles
        String[] roles = {"Sin rol", "Administrador", "Empleado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                roles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spRol.setAdapter(adapter);

        usuarioId = getArguments() != null
                ? getArguments().getString("usuarioId")
                : null;

        if (usuarioId == null) {
            Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(this).popBackStack();
            return binding.getRoot();
        }

        vm.cargarUsuarioPorId(usuarioId);

        // Observers
        vm.getPerfil().observe(getViewLifecycleOwner(), perfil -> {
            if (perfil == null) return;

            binding.etNombre.setText(perfil.getNombre());
            binding.etApellido.setText(perfil.getApellido());
            binding.etEmail.setText(perfil.getEmail());

            if (perfil.getRoles() == null || perfil.getRoles().isEmpty()) {
                binding.spRol.setSelection(0); // Sin rol
            } else if (perfil.getRoles().contains("Administrador")) {
                binding.spRol.setSelection(1);
            } else if (perfil.getRoles().contains("Empleado")) {
                binding.spRol.setSelection(2);
            } else {
                binding.spRol.setSelection(0); // fallback
            }
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

        // Guardar
        binding.btnGuardar.setOnClickListener(v -> {
            String rolSeleccionado = binding.spRol.getSelectedItem().toString();

            if ("Sin rol".equals(rolSeleccionado)) {
                Toast.makeText(
                        getContext(),
                        "Si el usuario tiene un rol asignado, no se podrá eliminar hasta quitarlo",
                        Toast.LENGTH_SHORT
                ).show();
            }

            vm.guardarCambios(
                    binding.etNombre.getText().toString().trim(),
                    binding.etApellido.getText().toString().trim(),
                    binding.etEmail.getText().toString().trim(),
                    rolSeleccionado
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
