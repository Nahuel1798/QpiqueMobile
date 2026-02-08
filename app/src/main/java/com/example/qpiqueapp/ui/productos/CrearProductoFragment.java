package com.example.qpiqueapp.ui.productos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentCrearProductoBinding;
import com.example.qpiqueapp.modelo.categoria.Categorias;

import java.util.ArrayList;
import java.util.List;

public class CrearProductoFragment extends Fragment {

    private FragmentCrearProductoBinding binding;
    private CrearProductoViewModel vm;
    private List<Categorias> listaCategorias = new ArrayList<>();

    private final ActivityResultLauncher<Intent> selectImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK &&
                                result.getData() != null) {
                            Uri uri = result.getData().getData();
                            vm.setImagen(uri);
                        }
                    });

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentCrearProductoBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(CrearProductoViewModel.class);

        vm.cargarCategorias();

        vm.getCategorias().observe(getViewLifecycleOwner(), categorias -> {
            if (categorias != null && !categorias.isEmpty()) {

                listaCategorias = categorias;

                List<String> nombresCategorias = new ArrayList<>();
                for (Categorias c : categorias) {
                    nombresCategorias.add(c.getNombre());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        nombresCategorias
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spCategorias.setAdapter(adapter);
            }
        });
        if (savedInstanceState == null) {
            vm.cargarCategorias();
        }

        // Imagen
        vm.getImagenSeleccionada().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .placeholder(R.drawable.ic_productos)
                        .into(binding.ivProductoImagen);
            }
        });

        // Mensajes
        vm.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                vm.mensajeConsumido();
            }
        });

        // Volver
        vm.getVolverAtras().observe(getViewLifecycleOwner(), volver -> {
            if (Boolean.TRUE.equals(volver)) {
                NavHostFragment.findNavController(this).popBackStack();
                vm.volverConsumido();
            }
        });

        // Botones
        binding.btnSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            selectImageLauncher.launch(intent);
        });

        binding.btnGuardar.setOnClickListener(v -> {
            int pos = binding.spCategorias.getSelectedItemPosition();
            Categorias cat = listaCategorias.get(pos);

            vm.crearProducto(
                    binding.etNombre.getText().toString().trim(),
                    binding.etDescripcion.getText().toString().trim(),
                    binding.etPrecio.getText().toString().trim(),
                    binding.etStock.getText().toString().trim(),
                    cat.getId()
            );
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
