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

import com.example.qpiqueapp.databinding.FragmentBorrarClienteBinding;
import com.example.qpiqueapp.modelo.clientes.Clientes;

public class BorrarClienteFragment extends Fragment {

    private FragmentBorrarClienteBinding binding;
    private BorrarClienteViewModel vm;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentBorrarClienteBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(BorrarClienteViewModel.class);

        Clientes cliente =
                (Clientes) getArguments().getSerializable("cliente");

        binding.tvMensaje.setText(
                "¿Desea eliminar el cliente " + cliente.getNombre() + "?"
        );

        configurarUI(cliente);
        observarViewModel();

        return binding.getRoot();
    }

    private void configurarUI(Clientes cliente) {

        binding.btnConfirmar.setOnClickListener(v ->
                vm.eliminarCliente(cliente.getId())
        );

        binding.btnCancelar.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack()
        );
    }

    private void observarViewModel() {

        vm.getLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnConfirmar.setEnabled(!loading);
        });

        vm.getMensaje().observe(getViewLifecycleOwner(), msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        );

        vm.getNavegarAtras().observe(getViewLifecycleOwner(), volver -> {
            if (Boolean.TRUE.equals(volver)) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
