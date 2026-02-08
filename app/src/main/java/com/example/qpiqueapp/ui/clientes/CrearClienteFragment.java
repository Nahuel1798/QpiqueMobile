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

import com.example.qpiqueapp.databinding.FragmentCrearClienteBinding;

public class CrearClienteFragment extends Fragment {

    private FragmentCrearClienteBinding binding;
    private CrearClienteViewModel vm;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        binding = FragmentCrearClienteBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(CrearClienteViewModel.class);

        configurarUI();
        observarViewModel();

        return binding.getRoot();
    }

    private void configurarUI() {
        binding.btnGuardarCliente.setOnClickListener(v ->
                vm.guardarCliente(
                        binding.etNombre.getText().toString(),
                        binding.etApellido.getText().toString(),
                        binding.etTelefono.getText().toString(),
                        binding.etEmail.getText().toString()
                )
        );
    }

    private void observarViewModel() {

        vm.getMensaje().observe(getViewLifecycleOwner(), msg ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show()
        );

        vm.getNavegarAtras().observe(getViewLifecycleOwner(), navegar -> {
            if (Boolean.TRUE.equals(navegar)) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        vm.getLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnGuardarCliente.setEnabled(!loading);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

