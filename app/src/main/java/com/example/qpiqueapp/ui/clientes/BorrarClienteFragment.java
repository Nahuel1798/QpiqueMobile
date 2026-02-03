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

import com.example.qpiqueapp.databinding.FragmentBorrarClienteBinding;
import com.example.qpiqueapp.modelo.Clientes;

public class BorrarClienteFragment extends Fragment {
    private FragmentBorrarClienteBinding binding;
    private BorrarClienteViewModel vm;
    private Clientes cliente;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBorrarClienteBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(BorrarClienteViewModel.class);
        cliente = (Clientes) getArguments().getSerializable("cliente");
        binding.tvMensaje.setText("¿Desea eliminar el cliente " + cliente.getNombre() + "?");
        binding.btnConfirmar.setOnClickListener(v -> {
            vm.eliminarCliente(cliente.getId());
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
}