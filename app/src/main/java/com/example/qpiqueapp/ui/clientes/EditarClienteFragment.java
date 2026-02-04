package com.example.qpiqueapp.ui.clientes;

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
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this).get(EditarClienteViewModel.class);

        Clientes cliente =
                (Clientes) getArguments().getSerializable("cliente");
        vm.inicializar(cliente);

        // Observer

        vm.getCliente().observe(getViewLifecycleOwner(), c -> {
            if (c == null) return;
            binding.etNombre.setText(c.getNombre());
            binding.etApellido.setText(c.getApellido());
            binding.etTelefono.setText(c.getTelefono());
            binding.etEmail.setText(c.getEmail());
        });

        vm.getLoading().observe(getViewLifecycleOwner(), loading ->
                binding.btnGuardar.setEnabled(!Boolean.TRUE.equals(loading))
        );

        vm.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                vm.mensajeConsumido();
            }
        });

        vm.getVolverAtras().observe(getViewLifecycleOwner(), volver -> {
            if (Boolean.TRUE.equals(volver)) {
                NavHostFragment.findNavController(this).popBackStack();
                vm.volverConsumido();
            }
        });

        // Acciones de botnes

        binding.btnGuardar.setOnClickListener(v ->
                vm.guardarCambios(
                        binding.etNombre.getText().toString(),
                        binding.etApellido.getText().toString(),
                        binding.etTelefono.getText().toString(),
                        binding.etEmail.getText().toString()
                )
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
