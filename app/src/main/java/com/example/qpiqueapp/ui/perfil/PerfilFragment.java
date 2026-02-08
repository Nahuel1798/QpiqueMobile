package com.example.qpiqueapp.ui.perfil;

import static com.example.qpiqueapp.request.ApiClient.BASE_URL;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel vm;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(PerfilViewModel.class);

        SharedPreferences prefs =
                requireContext().getSharedPreferences("auth", 0);

        String token = prefs.getString("jwt", null);

        if (token != null) {
            vm.cargarPerfil(token);
        } else {
            Toast.makeText(getContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show();
        }

        observarViewModel();
        configurarBotones();

        return binding.getRoot();
    }

    private void observarViewModel() {
        vm.getPerfilLiveData().observe(getViewLifecycleOwner(), perfil -> {
            if (perfil == null) return;

            binding.tvNombre.setText(perfil.nombre + " " + perfil.apellido);
            binding.tvEmail.setText(perfil.email);
            binding.tvRoles.setText(String.join(", ", perfil.roles));

            String avatarUrl = perfil.avatar;
            if (avatarUrl != null && !avatarUrl.startsWith("http")) {
                avatarUrl = BASE_URL + avatarUrl;
            }

            Glide.with(requireContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_settings_black_24dp)
                    .error(R.drawable.ic_settings_black_24dp)
                    .circleCrop()
                    .into(binding.ivProfileAvatar);
        });

        vm.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarBotones() {
        binding.btnLogout.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_perfilFragment_to_logoutFragment)
        );

        binding.btnEditProfile.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_perfilFragment_to_editarFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
