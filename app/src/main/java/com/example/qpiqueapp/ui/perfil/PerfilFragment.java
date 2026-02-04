package com.example.qpiqueapp.ui.perfil;

import static com.example.qpiqueapp.request.ApiClient.BASE_URL;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentClientesBinding;
import com.example.qpiqueapp.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel vm;
    private TextView tvnombre;
    private TextView tvemail;
    private TextView tvrol;
    private ImageView tvAvatar;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        tvnombre = view.findViewById(R.id.tvNombre);
        tvemail = view.findViewById(R.id.tvEmail);
        tvrol = view.findViewById(R.id.tvRoles);
        tvAvatar = view.findViewById(R.id.ivProfileAvatar);


        vm = new ViewModelProvider(this).get(PerfilViewModel.class);
        SharedPreferences prefs =
                requireContext().getSharedPreferences("auth", 0);

        String token = prefs.getString("jwt", null);

        if (token != null) {
            vm.cargarPerfil(token);
        } else {
            Toast.makeText(getContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show();
        }

        vm.getPerfilLiveData().observe(getViewLifecycleOwner(), perfil -> {
            if (perfil != null) {
                tvnombre.setText(perfil.nombre + " " + perfil.apellido);
                tvemail.setText(perfil.email);
                tvrol.setText(String.join(", ", perfil.roles));

                String baseUrl = BASE_URL;
                String avatarUrl = perfil.avatar;

                // Construir la URL final del avatar
                if (avatarUrl != null && !avatarUrl.startsWith("http")) {
                    avatarUrl = baseUrl + avatarUrl;
                }

                Log.d("PerfilFragment", "URL final del avatar: " + avatarUrl);

                Glide.with(requireContext())
                        .load(avatarUrl)
                        .placeholder(R.drawable.ic_settings_black_24dp)
                        .error(R.drawable.ic_settings_black_24dp)
                        .circleCrop()
                        .into(tvAvatar);
            }
        });

        binding.btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(),"Cerrando sesión", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.action_perfilFragment_to_logoutFragment);
        });

        binding.btnEditProfile.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_perfilFragment_to_editarFragment);
        });


        vm.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}