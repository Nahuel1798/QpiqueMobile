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
import com.example.qpiqueapp.databinding.FragmentEditarProductoBinding;
import com.example.qpiqueapp.modelo.Categorias;
import com.example.qpiqueapp.modelo.Productos;
import com.example.qpiqueapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class EditarProductoFragment extends Fragment {

    private FragmentEditarProductoBinding binding;
    private EditarProductoViewModel vm;
    private List<Categorias> listaCategorias;
    private Productos producto;

    /* ===================== SELECTOR DE IMAGEN ===================== */

    private final ActivityResultLauncher<Intent> selectImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK &&
                                result.getData() != null &&
                                result.getData().getData() != null) {

                            Uri imagenUri = result.getData().getData();

                            // Guardar en ViewModel
                            vm.setImagenSeleccionada(imagenUri);

                            // Preview
                            Glide.with(this)
                                    .load(imagenUri)
                                    .circleCrop()
                                    .into(binding.ivImagen);
                        }
                    }
            );

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentEditarProductoBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(EditarProductoViewModel.class);

        /* ===================== RECIBIR PRODUCTO ===================== */

        // Obtener Categoria
        vm.getCategoria().observe(getViewLifecycleOwner(), categorias -> {
            if (categorias != null && !categorias.isEmpty()){
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
                binding.spCategoria.setAdapter(adapter);
            }
        });

        vm.cargarCategorias();

        // Obtener producto

        if (getArguments() != null) {
            producto = (Productos) getArguments().getSerializable("producto");
        }

        if (producto == null) {
            Toast.makeText(getContext(), "Producto no encontrado", Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(this).popBackStack();
            return binding.getRoot();
        }

        vm.setProducto(producto);

        /* ===================== OBSERVERS ===================== */

        vm.getProducto().observe(getViewLifecycleOwner(), p -> {
            binding.etNombre.setText(p.getNombre());
            binding.etDescripcion.setText(p.getDescripcion());
            binding.etPrecio.setText(String.valueOf(p.getPrecio()));
            binding.etStock.setText(String.valueOf(p.getStock()));

            if (listaCategorias != null && !listaCategorias.isEmpty()) {
                int posicionSpinner = -1;
                // 2. Buscar la posición del ID de la categoría del producto
                for (int i = 0; i < listaCategorias.size(); i++) {
                    if (listaCategorias.get(i).getId() == p.getCategoriaId()) {
                        posicionSpinner = i;
                        break; // Salimos del bucle una vez encontrada
                    }
                }

                // 3. Seleccionar la posición en el Spinner
                if (posicionSpinner != -1) {
                    binding.spCategoria.setSelection(posicionSpinner);
                }
            }

            // Cargar imagen solo si el usuario no eligió otra
            if (vm.getImagenSeleccionada().getValue() != null) return;

            String imagenUrl = p.getImagenUrl();
            if (imagenUrl != null && !imagenUrl.startsWith("http")) {
                imagenUrl = ApiClient.BASE_URL + imagenUrl;
            }

            Glide.with(this)
                    .load(imagenUrl)
                    .placeholder(R.drawable.ic_productos)
                    .error(R.drawable.ic_productos)
                    .circleCrop()
                    .into(binding.ivImagen);
        });

        vm.getEstado().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            binding.btnGuardar.setEnabled(!state.loading);

            if (state.success) {
                Toast.makeText(getContext(),
                        "Producto actualizado correctamente",
                        Toast.LENGTH_SHORT).show();

                NavHostFragment.findNavController(this).popBackStack();
            }

            if (state.error != null) {
                Toast.makeText(getContext(), state.error, Toast.LENGTH_SHORT).show();
            }
        });

        vm.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        vm.getVolverAtras().observe(getViewLifecycleOwner(), volver -> {
            if (Boolean.TRUE.equals(volver)) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        });


        /* ===================== ACCIONES ===================== */

        binding.btnCambiarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            selectImageLauncher.launch(intent);
        });

        binding.btnGuardar.setOnClickListener(v -> {
            String nombre = binding.etNombre.getText().toString().trim();
            String descripcion = binding.etDescripcion.getText().toString().trim();
            String precioStr = binding.etPrecio.getText().toString().trim();
            String stockStr = binding.etStock.getText().toString().trim();
            // Obtener la posición seleccionada en el Spinner
            int posicionSeleccionada = binding.spCategoria.getSelectedItemPosition();

            // Validar que la posición y la lista sean válidas
            if (posicionSeleccionada >= 0 && listaCategorias != null && posicionSeleccionada < listaCategorias.size()) {
                // Obtener el ID de la categoría
                int categoriaId = listaCategorias.get(posicionSeleccionada).getId();

                vm.guardarCambios(
                        nombre,
                        descripcion,
                        precioStr,
                        stockStr,
                        categoriaId
                );
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
