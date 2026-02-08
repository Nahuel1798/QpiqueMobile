package com.example.qpiqueapp.ui.perfil;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qpiqueapp.databinding.FragmentLogoutBinding;
import com.example.qpiqueapp.ui.login.LoginActivity;

public class LogoutFragment extends Fragment {

    private FragmentLogoutBinding binding;
    private LogoutViewModel vm;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentLogoutBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(LogoutViewModel.class);

        observarViewModel();

        // Pedimos confirmación apenas entra al fragment
        vm.solicitarLogout();

        return binding.getRoot();
    }

    private void observarViewModel() {
        vm.getMostrarDialogo().observe(getViewLifecycleOwner(), mostrar -> {
            if (mostrar != null && mostrar) {
                mostrarDialogoConfirmacion();
            }
        });

        vm.getRedirigirLogin().observe(getViewLifecycleOwner(), redirigir -> {
            if (redirigir != null && redirigir) {
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void mostrarDialogoConfirmacion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> vm.cerrarSesion())
                .setNegativeButton("Cancelar", (dialog, which) ->
                        requireActivity().getOnBackPressedDispatcher().onBackPressed()
                )
                .setCancelable(false)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
