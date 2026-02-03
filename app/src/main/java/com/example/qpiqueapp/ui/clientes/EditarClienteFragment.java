package com.example.qpiqueapp.ui.clientes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentEditarClienteBinding;
import com.example.qpiqueapp.modelo.Clientes;

public class EditarClienteFragment extends Fragment {
    private FragmentEditarClienteBinding binding;
    private EditarClienteViewModel vm;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentEditarClienteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this).get(EditarClienteViewModel.class);

        Clientes cliente = (Clientes) getArguments().getSerializable("cliente");
        if (cliente == null) {
            Toast.makeText(getContext(), "Cliente no encontrado", Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }
        vm.setCliente(cliente);

        // Cargar Datos
        vm.getCliente().observe(getViewLifecycleOwner(), c -> {
            binding.etNombre.setText(c.getNombre());
            binding.etApellido.setText(c.getApellido());
            binding.etTelefono.setText(c.getTelefono());
            binding.etEmail.setText(c.getEmail());
        });

        // Guardar
        binding.btnGuardar.setOnClickListener(v ->
                vm.guardarCambios(
                        binding.etNombre.getText().toString(),
                        binding.etApellido.getText().toString(),
                        binding.etTelefono.getText().toString(),
                        binding.etEmail.getText().toString()
                )
        );
        // Estado
        vm.getEstado().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            if (state.loading) {
                binding.btnGuardar.setEnabled(false);
            } else {
                binding.btnGuardar.setEnabled(true);
            }
            if (state.success) {
                Toast.makeText(getContext(), "Cliente actualizado", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            }

            if (state.error != null) {
                Toast.makeText(getContext(), state.error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}