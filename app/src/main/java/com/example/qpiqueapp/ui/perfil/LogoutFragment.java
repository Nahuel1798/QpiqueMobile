package com.example.qpiqueapp.ui.perfil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qpiqueapp.databinding.FragmentLogoutBinding;
import com.example.qpiqueapp.ui.login.LoginActivity;

public class LogoutFragment extends Fragment {
    private FragmentLogoutBinding binding;
    private LogoutViewModel vm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLogoutBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(LogoutViewModel.class);

        // Observa cuando mostrar el dialogo
        vm.getMostrarDialogo().observe(getViewLifecycleOwner(), mostrar -> {
            if (mostrar != null && mostrar) {
                mostrarDialogoConfirmacion();
            }
        });

        // Observa cuando redirigir al Login
        vm.getRedirigirLogin().observe(getViewLifecycleOwner(), redirigir -> {
            if (redirigir != null && redirigir) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Botón para cerrar sesión
        vm.cerrarSesion();

        return binding.getRoot();
    }

    private void mostrarDialogoConfirmacion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que deseas cerrar sesión?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vm.cerrarSesion();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> requireActivity().onBackPressed())
                .setCancelable(false)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}