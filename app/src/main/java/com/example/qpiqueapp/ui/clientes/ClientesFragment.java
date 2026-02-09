package com.example.qpiqueapp.ui.clientes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast; // NUEVO: Necesario para el Toast

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentClientesBinding;
import com.example.qpiqueapp.modelo.clientes.Clientes;
import com.example.qpiqueapp.ui.ventas.VentasViewModel;

import java.util.ArrayList;


public class ClientesFragment extends Fragment {
    private FragmentClientesBinding binding;
    private ClientesViewModel vm;
    private ClientesAdapter adapter;
    private VentasViewModel ventasViewModel;
    private boolean modoSeleccion = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClientesBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(ClientesViewModel.class);
        ventasViewModel = new ViewModelProvider(requireActivity())
                .get(VentasViewModel.class);

        // Inicializar el Adapter pasándole el contexto y el fragmento como listener
        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        binding.listaClientes.setLayoutManager(glm);

        if (getArguments() != null) {
            modoSeleccion = getArguments().getBoolean("modoSeleccion", false);
        }

        // Ocultar botones si estamos en modo selección
//        if (modoSeleccion) {
//            binding.btnNuevoCliente.setVisibility(View.GONE);
//        }

        adapter = new ClientesAdapter(
                new ArrayList<>(),
                requireContext(),
                new ClientesAdapter.OnItemClickListener() {
                    @Override
                    public void onEditar(Clientes cliente) {
                        // Bloquear la acción si estamos en modo selección
                        if (modoSeleccion) {
                            Toast.makeText(getContext(), "Primero debe seleccionar un cliente", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("cliente", cliente);

                        NavHostFragment.findNavController(ClientesFragment.this)
                                .navigate(
                                        R.id.action_nav_slideshow_to_editarClienteFragment,
                                        bundle
                                );
                    }

                    @Override
                    public void onEliminar(Clientes cliente) {
                        // Bloquear la acción si estamos en modo selección
                        if (modoSeleccion) {
                            Toast.makeText(getContext(), "Primero debe seleccionar un cliente", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("cliente", cliente);

                        NavHostFragment.findNavController(ClientesFragment.this)
                                .navigate(
                                        R.id.action_nav_slideshow_to_borrarClienteFragment,
                                        bundle
                                );
                    }
                },
                clienteSeleccionado -> {
                    // Guarda el cliente en el ViewModel compartido.
                    ventasViewModel.setClienteSeleccionado(clienteSeleccionado);

                    // Mostramos un mensaje de confirmación
                    Toast.makeText(getContext(), clienteSeleccionado.getNombre() + " seleccionado", Toast.LENGTH_SHORT).show();

                    // Y volvemos al fragmento anterior (que será CarritoFragment)
                    NavHostFragment.findNavController(ClientesFragment.this).navigateUp();

                },
                modoSeleccion
        );
        binding.listaClientes.setAdapter(adapter);

        vm.getListaClientes().observe(getViewLifecycleOwner(), clientes -> {
            adapter.setClientes(clientes);
        });

        vm.cargarInicial();

        binding.searchViewClientes.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        binding.searchViewClientes.clearFocus();
                        binding.listaClientes.scrollToPosition(0);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty()) {
                            binding.listaClientes.scrollToPosition(0);
                        }
                        return true;
                    }
                }
        );

        binding.btnNuevoCliente.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_nav_slideshow_to_crearClienteFragment);
        });

        binding.listaClientes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (!rv.canScrollVertically(1)) {
                    vm.cargarMas();
                }
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
