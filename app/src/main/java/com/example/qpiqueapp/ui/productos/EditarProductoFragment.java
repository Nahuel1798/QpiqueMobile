package com.example.qpiqueapp.ui.productos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.example.qpiqueapp.modelo.categoria.Categorias;
import com.example.qpiqueapp.modelo.productos.Productos;
import com.example.qpiqueapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class EditarProductoFragment extends Fragment {

    private FragmentEditarProductoBinding binding;
    private EditarProductoViewModel vm;

    private List<Categorias> listaCategorias = new ArrayList<>();

    private Integer categoriaIdProducto = null;
    private boolean categoriasCargadas = false;

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

        binding = FragmentEditarProductoBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(EditarProductoViewModel.class);

        configurarPermisos();

        Productos producto =
                (Productos) getArguments().getSerializable("producto");
        vm.inicializar(producto);

        vm.cargarCategorias();

        // 🔹 Observer categorías → Spinner
        vm.getCategorias().observe(getViewLifecycleOwner(), cats -> {
            listaCategorias = cats;
            categoriasCargadas = true;

            List<String> nombres = new ArrayList<>();
            for (Categorias c : cats) {
                nombres.add(c.getNombre());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    nombres
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spCategoria.setAdapter(adapter);

            seleccionarCategoriaSiSePuede(); // 🔁 intentamos
        });

        // 🔹 Observer producto
        vm.getProducto().observe(getViewLifecycleOwner(), p -> {
            if (p == null) return;

            binding.etNombre.setText(p.getNombre());
            binding.etDescripcion.setText(p.getDescripcion());
            binding.etPrecio.setText(String.valueOf(p.getPrecio()));
            binding.etStock.setText(String.valueOf(p.getStock()));
            categoriaIdProducto = p.getCategoriaId();

            // Spinner
            seleccionarCategoriaSiSePuede();

            // Imagen
            if (vm.getImagenSeleccionada().getValue() == null) {
                String url = p.getImagenUrl();
                if (url != null && !url.startsWith("http")) {
                    url = ApiClient.BASE_URL + url;
                }

                Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.ic_productos)
                        .circleCrop()
                        .into(binding.ivImagen);
            }
        });

        vm.getImagenSeleccionada().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .placeholder(R.drawable.ic_productos)
                        .into(binding.ivImagen);
            }
        });

        vm.getLoading().observe(getViewLifecycleOwner(),
                l -> binding.btnGuardar.setEnabled(!Boolean.TRUE.equals(l)));

        vm.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                vm.mensajeConsumido();
            }
        });

        vm.getVolverAtras().observe(getViewLifecycleOwner(), volver -> {
            if (Boolean.TRUE.equals(volver)) {
                NavHostFragment.findNavController(this).popBackStack();
                vm.volverConsumido();
            }
        });

        // 🔘 Botones
        binding.btnCambiarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            selectImageLauncher.launch(intent);
        });

        binding.btnGuardar.setOnClickListener(v -> {

            if (listaCategorias.isEmpty()) {
                Toast.makeText(
                        getContext(),
                        "No hay categorías disponibles",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            int posicion = binding.spCategoria.getSelectedItemPosition();
            Categorias categoriaSeleccionada = listaCategorias.get(posicion);

            vm.guardarCambios(
                    binding.etNombre.getText().toString().trim(),
                    binding.etDescripcion.getText().toString().trim(),
                    binding.etPrecio.getText().toString().trim(),
                    binding.etStock.getText().toString().trim(),
                    categoriaSeleccionada.getId()
            );
        });

        return binding.getRoot();
    }

    private void seleccionarCategoriaSiSePuede() {

        if (!categoriasCargadas || categoriaIdProducto == null) return;

        for (int i = 0; i < listaCategorias.size(); i++) {
            if (listaCategorias.get(i).getId() == categoriaIdProducto) {
                binding.spCategoria.setSelection(i);
                break;

            }
            Log.d("EDITAR",
                    "Spinner ID=" + listaCategorias.get(i).getId() +
                            " Producto ID=" + categoriaIdProducto);
        }
    }

    private void configurarPermisos() {
        String rol = ApiClient.leerRol(requireContext());

        if (rol == null) return;

        if (!"Administrador".equalsIgnoreCase(rol)) {
            binding.etPrecio.setEnabled(false);
            binding.etPrecio.setFocusable(false);
            binding.etPrecio.setClickable(false);

            binding.tilPrecio.setHelperText(
                    "Solo administradores pueden modificar el precio"
            );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
