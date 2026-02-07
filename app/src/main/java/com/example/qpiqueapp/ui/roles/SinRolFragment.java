package com.example.qpiqueapp.ui.roles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.preference.PreferenceManager; // Para SharedPreferences

import com.example.qpiqueapp.databinding.FragmentSinRolBinding; // Importante: esta clase se genera automáticamente
import com.example.qpiqueapp.ui.login.LoginActivity; // Asegúrate que esta es tu Activity de Login

public class SinRolFragment extends Fragment {
    private FragmentSinRolBinding binding;

    public SinRolFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSinRolBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void logout() {
        if (getActivity() == null) {
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("AUTH_TOKEN"); // Cambia "AUTH_TOKEN" por la clave que uses
        editor.remove("USER_ID");    // Limpia cualquier otro dato relevante
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
