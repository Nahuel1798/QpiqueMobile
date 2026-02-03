package com.example.qpiqueapp.ui.perfil;

import static com.example.qpiqueapp.request.ApiClient.BASE_URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentEditarBinding;


public class EditarFragment extends Fragment {
    private FragmentEditarBinding binding;
    private EditarViewModel vm;
    private Uri avatarUri;

    private final ActivityResultLauncher<Intent> selectImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    avatarUri = result.getData().getData(); // Guardamos el URI
                    Glide.with(this)
                            .load(avatarUri)
                            .circleCrop()
                            .into(binding.ivAvatar);
                }
            });

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState){
        binding = FragmentEditarBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(EditarViewModel.class);
        //observar
        vm.getPerfilLiveData().observe(getViewLifecycleOwner(), perfil -> {
            binding.etNombre.setText(perfil.getNombre());
            binding.etApellido.setText(perfil.getApellido());
            binding.etEmail.setText(perfil.getEmail());

            if (avatarUri != null) return;

            String baseUrl = BASE_URL;
            String avatarUrl = perfil.avatar;

            if (avatarUrl != null && !avatarUrl.startsWith("http")) {
                avatarUrl = baseUrl + avatarUrl;
            }

            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_productos)
                    .error(R.drawable.ic_productos)
                    .circleCrop()
                    .into(binding.ivAvatar);
        });

        vm.getMensaje().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null) Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
        });

        binding.btnCambiarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            selectImageLauncher.launch(intent);
        });

        // Guardar cambios
        binding.btnGuardar.setOnClickListener(v -> {
            String nombre = binding.etNombre.getText().toString().trim();
            String apellido = binding.etApellido.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();

            vm.actualizarPerfil(nombre, apellido, email, avatarUri);
            NavHostFragment.findNavController(this).navigateUp();
        });

        vm.cargarPerfil();
        return binding.getRoot();
    }
}
