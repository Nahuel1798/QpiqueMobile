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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qpiqueapp.databinding.FragmentCrearProductoBinding;
import com.example.qpiqueapp.modelo.Categorias;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class CrearProductoFragment extends Fragment {
    private List<Categorias> listaCategorias;
    private CrearProductoViewModel vm;
    private FragmentCrearProductoBinding binding;
    private Uri imagenUri;

    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            imagenUri = result.getData().getData();
                            binding.ivProductoImagen.setImageURI(imagenUri);
                        }
                    }
            );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCrearProductoBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(CrearProductoViewModel.class);

        binding.btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen());
        binding.btnGuardar.setOnClickListener(v -> guardarProducto());

        vm.getExito().observe(getViewLifecycleOwner(), ok -> {
            if (ok) {
                Toast.makeText(getContext(), "Producto creado correctamente", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
        vm.getError().observe(getViewLifecycleOwner(), msg ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show()
        );
        vm.getCategoria().observe(getViewLifecycleOwner(), categorias -> {
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
        return binding.getRoot();
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePicker.launch(intent);
    }

    private void guardarProducto(){
        String nombre = binding.etNombre.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();
        String precio = binding.etPrecio.getText().toString().trim();
        String stock = binding.etStock.getText().toString().trim();
        int pos = binding.spCategorias.getSelectedItemPosition();
        int categoriaId = listaCategorias.get(pos).getId();


        if (nombre.isEmpty() || descripcion.isEmpty() ||
                precio.isEmpty() || stock.isEmpty()) {
            Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagenUri == null) {
            Toast.makeText(getContext(), "Seleccione una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        MultipartBody.Part imagenPart;
        try (InputStream inputStream =
                     requireContext().getContentResolver().openInputStream(imagenUri)) {

            File tempFile = new File(
                    requireContext().getCacheDir(),
                    "producto_" + System.currentTimeMillis() + ".jpg"
            );

            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
            }

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(requireContext().getContentResolver().getType(imagenUri)),
                    tempFile
            );

            imagenPart = MultipartBody.Part.createFormData(
                    "Imagen",
                    tempFile.getName(),
                    requestFile
            );

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
            return;
        }
        vm.crearProducto(nombre, descripcion, precio, stock, String.valueOf(categoriaId), imagenPart);
    }
}

