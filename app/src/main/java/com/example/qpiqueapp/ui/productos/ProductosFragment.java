package com.example.qpiqueapp.ui.productos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private CarritoViewModel carritoVM;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProductosBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(ProductosViewModel.class);
        carritoVM = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);

        configurarRecycler();
        observarViewModel();
        configurarBuscador();
        configurarBotones();

        vm.cargarCategorias();
        vm.cargarInicial();

        return binding.getRoot();
    }

    private void configurarRecycler() {
        binding.listaProductos.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new ProductosAdapter(
                new ArrayList<>(),
                requireContext(),
                getLayoutInflater(),
                new ProductosAdapter.OnItemClickListener() {
                    @Override
                    public void onEditar(Productos p) {
                        Bundle b = new Bundle();
                        b.putSerializable("producto", p);
                        NavHostFragment.findNavController(ProductosFragment.this)
                                .navigate(R.id.action_nav_transform_to_editarProductoFragment, b);
                    }

                    @Override
                    public void onEliminar(Productos p) {
                        Bundle b = new Bundle();
                        b.putSerializable("producto", p);
                        NavHostFragment.findNavController(ProductosFragment.this)
                                .navigate(R.id.action_nav_transform_to_borrarFragment, b);
                    }

                    @Override
                    public void onAgregarCarrito(Productos p) {
                        carritoVM.agregarProducto(p);
                        Toast.makeText(getContext(), "Producto agregado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onQuitarCarrito(Productos p) {
                        carritoVM.quitarProducto(p);
                        Toast.makeText(getContext(), "Producto quitado", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        binding.listaProductos.setAdapter(adapter);

        binding.listaProductos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (!rv.canScrollVertically(1)) {
                    vm.cargarMas();
                }
            }
        });
    }

    private void observarViewModel() {
        vm.getListaProductos().observe(getViewLifecycleOwner(), adapter::setProductos);

        vm.getCategorias().observe(getViewLifecycleOwner(), categorias -> {
            AutoCompleteTextView sp = binding.spCategorias;
            ArrayAdapter<Categorias> adapter =
                    new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            categorias);
            sp.setAdapter(adapter);

            sp.setOnItemClickListener((p, v, pos, id) -> {
                Categorias c = (Categorias) p.getItemAtPosition(pos);
                vm.seleccionarCategoria(c.getId() == 0 ? null : c.getId());
            });
        });

        carritoVM.getCarrito().observe(getViewLifecycleOwner(), lista -> {
            int cantidad = lista.size();
            binding.tvContadorCarrito.setText(String.valueOf(cantidad));
            binding.tvContadorCarrito.setVisibility(cantidad > 0 ? View.VISIBLE : View.GONE);

            Set<Integer> ids = lista.stream()
                    .map(Productos::getId)
                    .collect(Collectors.toSet());

            adapter.setProductosAgregados(ids);
        });
    }

    private void configurarBuscador() {
        binding.searchViewProductos.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String q) {
                        vm.buscar(q);
                        binding.searchViewProductos.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String t) {
                        if (t.isEmpty()) vm.buscar(null);
                        return true;
                    }
                });
    }

    private void configurarBotones() {
        binding.btnNuevoProducto.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_nav_transform_to_crearProductoFragment)
        );

        binding.fabCarrito.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_nav_transform_to_carritoFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
