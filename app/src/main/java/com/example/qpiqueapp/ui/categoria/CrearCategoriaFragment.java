package com.example.qpiqueapp.ui.categoria;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qpiqueapp.databinding.FragmentCrearCategoriaBinding;

public class CrearCategoriaFragment extends Fragment {
    private FragmentCrearCategoriaBinding binding;
    private CrearCategoriaViewModel vm;

    // Imagen
    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri uri = result.getData().getData();
                            vm.onImagenSeleccionada(uri);
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentCrearCategoriaBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(CrearCategoriaViewModel.class);

        // Observadores
        vm.getAbrirSelectorImagen().observe(getViewLifecycleOwner(), abrir -> {
            if (Boolean.TRUE.equals(abrir)) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                imagePicker.launch(intent);
                vm.eventoImagenConsumido();
            }
        });

        vm.getImagenSeleccionada().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                binding.ivImagenCategoria.setImageURI(uri);
            }
        });

        vm.getCreado().observe(getViewLifecycleOwner(), ok -> {
            if (Boolean.TRUE.equals(ok)) {
                Toast.makeText(getContext(), "Categoría creada correctamente", Toast.LENGTH_SHORT).show();
                vm.creadoConsumido();
                requireActivity().onBackPressed();
            }
        });

        vm.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                vm.errorMostrado();
            }
        });

        // Acciones de boton
        binding.btnSeleccionarImagen.setOnClickListener(v ->
                vm.solicitarSeleccionImagen()
        );

        binding.btnGuardarCategoria.setOnClickListener(v ->
                vm.crearCategoria(
                        requireContext(),
                        binding.etNombreCategoria.getText().toString().trim()
                )
        );

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

