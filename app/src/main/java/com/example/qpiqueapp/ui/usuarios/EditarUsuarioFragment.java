package com.example.qpiqueapp.ui.usuarios;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.qpiqueapp.databinding.FragmentEditarUsuarioBinding;
import com.example.qpiqueapp.modelo.PerfilDto;

import java.util.List;

public class EditarUsuarioFragment extends Fragment {
    private FragmentEditarUsuarioBinding binding;
    private EditarUsuarioViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditarUsuarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this).get(EditarUsuarioViewModel.class);

        // Spinner roles
        String[] roles = {"Administrador", "Empleado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spRol.setAdapter(adapter);

        // Obtener ID del usuario
        String usuarioId = getArguments() != null ? getArguments().getString("usuarioId") : null;
        if (usuarioId == null) {
            Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }

        // Cargar usuario
        vm.cargarUsuarioPorId(usuarioId);

        // 🔹 Observer del perfil
        vm.getPerfilLiveData().observe(getViewLifecycleOwner(), perfil -> {
            if (perfil == null) return;

            binding.etNombre.setText(perfil.getNombre());
            binding.etApellido.setText(perfil.getApellido());
            binding.etEmail.setText(perfil.getEmail());
            boolean esAdmin = perfil.getRoles().contains("Administrador");
            binding.spRol.setSelection(esAdmin ? 0 : 1);
        });

        // 🔹 Observer del estado
        vm.getEstado().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            binding.btnGuardar.setEnabled(!state.loading);

            if (state.error != null) {
                Toast.makeText(getContext(), state.error, Toast.LENGTH_LONG).show();
            }
        });

        // 🔹 Botón guardar
        binding.btnGuardar.setOnClickListener(v -> {
            String nombre = binding.etNombre.getText().toString().trim();
            String apellido = binding.etApellido.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            List<String> rol = binding.spRol.getSelectedItem().toString().equals("Administrador") ?
                    List.of("Administrador") : List.of("Empleado");

            vm.guardarCambios(nombre, apellido, email, rol);
        });
    }
}