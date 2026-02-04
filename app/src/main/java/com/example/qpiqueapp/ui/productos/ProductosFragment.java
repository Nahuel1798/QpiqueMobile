package com.example.qpiqueapp.ui.productos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentProductosBinding;
import com.example.qpiqueapp.modelo.categoria.Categorias;
import com.example.qpiqueapp.modelo.productos.Productos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class ProductosFragment extends Fragment {

    private FragmentProductosBinding binding;
    private ProductosViewModel vm;
    private ProductosAdapter adapter;
    private CarritoViewModel carritoViewModel;
    private boolean primeraSeleccion = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductosBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(ProductosViewModel.class);
        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);

        // Inicializar el Adapter
        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        binding.listaProductos.setLayoutManager(glm);

        // Adapter vacio inicial
        adapter = new ProductosAdapter(
                new ArrayList<>(),
                requireContext(),
                getLayoutInflater(),
                new ProductosAdapter.OnItemClickListener() {
                    @Override
                    public void onEditar(Productos producto) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("producto", producto);

                        NavHostFragment.findNavController(ProductosFragment.this)
                                .navigate(
                                        R.id.action_nav_transform_to_editarProductoFragment,
                                        bundle
                                );
                    }

                    @Override
                    public void onEliminar(Productos producto) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("producto", producto);

                        NavHostFragment.findNavController(ProductosFragment.this)
                                .navigate(
                                        R.id.action_nav_transform_to_borrarFragment,
                                        bundle
                                );
                    }

                    @Override
                    public void onAgregarCarrito(Productos producto) {
                        carritoViewModel.agregarProducto(producto);
                        Toast.makeText(getContext(), "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onQuitarCarrito(Productos producto) {
                        carritoViewModel.quitarProducto(producto);
                        Toast.makeText(getContext(), "Producto quitado del carrito", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        binding.listaProductos.setAdapter(adapter);

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
        vm.getListaProductos().observe(getViewLifecycleOwner(), productos -> {
            adapter.setProductos(productos);
        });

        carritoViewModel.getCarrito().observe(getViewLifecycleOwner(), lista -> {
            int cantidad = lista.size();
            binding.tvContadorCarrito.setText(String.valueOf(cantidad));
            binding.tvContadorCarrito.setVisibility(cantidad > 0 ? View.VISIBLE : View.GONE);

            // Informar al adapter sobre que productos están en el carrito.
            if (lista != null) {
                Set<Integer> idsEnCarrito = lista.stream()
                        .map(producto -> producto.getId())
                        .collect(Collectors.toSet());
                adapter.setProductosAgregados(idsEnCarrito);
            } else {
                adapter.setProductosAgregados(new HashSet<>()); // Enviar un conjunto vacío si la lista es nula
            }
        });

        // Cargas iniciales
        vm.cargarCategorias();
        vm.cargarInicial();

        // Buscador
        binding.searchViewProductos.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        vm.buscar(query);
                        binding.searchViewProductos.clearFocus();
                        binding.listaProductos.scrollToPosition(0);
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty()) {
                            vm.buscar("");
                            binding.listaProductos.scrollToPosition(0);
                        }
                        return true;
                    }
                }
        );

        // Boton Nuevo Producto
        binding.btnNuevoProducto.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_nav_transform_to_crearProductoFragment);
        });

        // Boton Carrito
        binding.fabCarrito.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_nav_transform_to_carritoFragment);
        });

        // Scroll infinito
        binding.listaProductos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (!rv.canScrollVertically(1)) {
                    vm.cargarMas();
                }
            }
        });

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

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
