package com.example.qpiqueapp.ui.categoria;

import static com.example.qpiqueapp.request.ApiClient.BASE_URL;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentEditarCategoriaBinding;
import com.example.qpiqueapp.modelo.Categorias;

public class EditarCategoriaFragment extends Fragment {

    private FragmentEditarCategoriaBinding binding;
    private EditarCategoriaViewModel vm;

    private final ActivityResultLauncher<Intent> selectImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK &&
                                result.getData() != null) {

                            Uri uri = result.getData().getData();
                            vm.setImagen(uri);
                        }
                    }
            );

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentEditarCategoriaBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(EditarCategoriaViewModel.class);

        Categorias categoria =
                (Categorias) getArguments().getSerializable("categoria");
        vm.inicializar(categoria);

        // Observer

        vm.getCategoria().observe(getViewLifecycleOwner(), c -> {
            if (c == null) return;
            binding.etNombre.setText(c.getNombre());

            Glide.with(this)
                    .load(BASE_URL + c.getImagenUrl())
                    .placeholder(R.drawable.ic_categoria)
                    .circleCrop()
                    .into(binding.ivImagenActual);
        });

        vm.getImagenSeleccionada().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .into(binding.ivImagenActual);
            }
        });

        vm.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                vm.mensajeConsumido();
            }
        });

        vm.getVolverAtras().observe(getViewLifecycleOwner(), volver -> {
            if (Boolean.TRUE.equals(volver)) {
                NavHostFragment.findNavController(this).navigateUp();
                vm.volverConsumido();
            }
        });

        // Acciones de boton

        binding.btnCambiarImagen.setOnClickListener(v -> abrirGaleria());

        binding.btnGuardar.setOnClickListener(v ->
                vm.guardarCambios(
                        requireContext(),
                        binding.etNombre.getText().toString()
                )
        );

        return binding.getRoot();
    }

    // Eventos
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        selectImageLauncher.launch(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
