package com.example.qpiqueapp.ui.ventas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentSeleccionarVPBinding;
import com.example.qpiqueapp.modelo.categoria.Categorias;
import com.example.qpiqueapp.modelo.productos.Productos;

import java.util.ArrayList;


public class SeleccionarVPFragment extends Fragment {
    private FragmentSeleccionarVPBinding binding;
    private SeleccionarVPVIewModel vm;
    private SeleccionarProductoAdapter adapter;
    private boolean primeraSeleccion = true;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSeleccionarVPBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(SeleccionarVPVIewModel.class);

        // Inicializar el adapter
        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        binding.listaVP.setLayoutManager(glm);

        adapter = new SeleccionarProductoAdapter(
                new ArrayList<>(),
                requireContext()
        );

        binding.listaVP.setAdapter(adapter);

        binding.listaVP.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (!rv.canScrollVertically(1)) {
                    vm.cargarMas();
                }
            }
        });

        AutoCompleteTextView autoCompleteTextView = binding.spCategorias;

        vm.getCategorias().observe(getViewLifecycleOwner(), categorias -> {
            if (categorias == null || categorias.isEmpty()) return;
            ArrayAdapter<Categorias> categoriaAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    categorias
            );
            autoCompleteTextView.setAdapter(categoriaAdapter);
            // Listener para cuando el usuario selecciona un item
            autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                Categorias categoriaSeleccionada = (Categorias) parent.getItemAtPosition(position);
                if (categoriaSeleccionada != null) {
                    Integer categoriaId = (categoriaSeleccionada.getId() == 0) ? null : categoriaSeleccionada.getId();
                    vm.seleccionarCategoria(categoriaId);
                }
            });
        });

        // Productos
        vm.getProductosSeleccionados().observe(getViewLifecycleOwner(), productos -> {
            adapter.setProductos(productos);
        });

        // Cargas iniciales
        vm.cargarCategorias();
        vm.cargarProductos();

        // Buscador
        binding.searchViewProducts.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        vm.buscar(query);
                        binding.searchViewProducts.clearFocus();
                        binding.listaVP.scrollToPosition(0);
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty()) {
                            vm.buscar("");
                            binding.listaVP.scrollToPosition(0);
                        }
                        return true;
                    }
                }
        );

        // Selección de categoría
        binding.spCategorias.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {

                        if (primeraSeleccion) {
                            primeraSeleccion = false;
                            return;
                        }

                        Categorias categoria =
                                (Categorias) parent.getItemAtPosition(position);

                        Integer categoriaId =
                                (categoria.getId() == 0) ? null : categoria.getId();

                        vm.seleccionarCategoria(categoriaId);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        binding.buttonDone.setOnClickListener(v -> {

            ArrayList<Productos> seleccionados =
                    new ArrayList<>(adapter.getSeleccionados());

            if (seleccionados.isEmpty()) {
                Toast.makeText(
                        getContext(),
                        "Seleccioná al menos un producto",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("productos", seleccionados);

            NavHostFragment.findNavController(this)
                    .navigate(
                            R.id.action_seleccionarVPFragment_to_editarVentaFragment,
                            bundle
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