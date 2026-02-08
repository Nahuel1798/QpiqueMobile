package com.example.qpiqueapp.ui.categoria;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentCategoriasBinding;
import com.example.qpiqueapp.modelo.categoria.Categorias;

import java.util.ArrayList;

public class CategoriasFragment extends Fragment {

    private FragmentCategoriasBinding binding;
    private CategoriasViewModel vm;
    private CategoriasAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoriasBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(CategoriasViewModel.class);

        // Configurar RecyclerView
        adapter = new CategoriasAdapter(
                new ArrayList<>(),
                requireContext(),
                getLayoutInflater(),
                new CategoriasAdapter.OnItemClickListener() {
                    @Override
                    public void onEditar(Categorias categoria) {
                        vm.seleccionarEditar(categoria);
                    }

                    @Override
                    public void onEliminar(Categorias categoria) {
                        vm.seleccionarEliminar(categoria);
                    }
                }
        );
        binding.listaCategorias.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.listaCategorias.setAdapter(adapter);

        // Observadores
        vm.getListaCategorias().observe(getViewLifecycleOwner(), categorias -> adapter.setCategorias(categorias));

        // Boton Editar (manda Bundle)
        vm.getCategoriaSeleccionadaEditar().observe(getViewLifecycleOwner(), categoria -> {
            if (categoria != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("categoria", categoria);
                NavHostFragment.findNavController(this).navigate(R.id.action_nav_settings_to_editarCategoriaFragment, bundle);
                vm.navegarCompletado();
            }
        });

        // Boton Eliminar (manda Bundle)
        vm.getCategoriaSeleccionadaEliminar().observe(getViewLifecycleOwner(), categoria -> {
            if (categoria != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("categoria", categoria);
                NavHostFragment.findNavController(this).navigate(R.id.action_nav_settings_to_eliminarCategoriaFragment, bundle);
                vm.navegarCompletado();
            }
        });

        // Boton Crear
        vm.getNavegarCrearCategoria().observe(getViewLifecycleOwner(), navegar -> {
            if (Boolean.TRUE.equals(navegar)) {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_nav_settings_to_crearCategoriaFragment);
                vm.navegarCompletado();
            }
        });

        // Observador de errores
        vm.getMensajeError().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
                vm.mensajeErrorVisto();
            }
        });

        // Boton nuevo
        binding.btnNuevoCliente.setOnClickListener(v -> vm.nuevoCategoria());

        // Cargar datos
        vm.cargarCategorias();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
