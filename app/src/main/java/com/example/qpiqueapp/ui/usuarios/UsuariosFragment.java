package com.example.qpiqueapp.ui.usuarios;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentUsuariosBinding;
import com.example.qpiqueapp.modelo.perfil.PerfilDto;

import java.util.ArrayList;

public class UsuariosFragment extends Fragment {
    private FragmentUsuariosBinding binding;
    private UsuariosViewModel vm;
    private UsuariosAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsuariosBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(requireActivity()).get(UsuariosViewModel.class);

        // Inicializar el adapter
        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        binding.listaUsuarios.setLayoutManager(glm);

        adapter = new UsuariosAdapter(
                new ArrayList<>(),
                requireContext(),
                LayoutInflater.from(getContext()),
                new UsuariosAdapter.OnItemClickListener() {
                    @Override
                    public void onEditar(PerfilDto usuario) {
                        Bundle bundle = new Bundle();
                        bundle.putString("usuarioId", usuario.getId());
                        NavHostFragment.findNavController(UsuariosFragment.this)
                                .navigate(R.id.action_usuariosFragment2_to_editarUsuarioFragment, bundle);

                    }
                    @Override
                    public void onEliminar(PerfilDto usuario) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("usuario", usuario);
                        NavHostFragment.findNavController(UsuariosFragment.this)
                                .navigate(R.id.action_usuariosFragment2_to_eliminarUsuarioFragment, bundle);
                    }
                });

        binding.listaUsuarios.setAdapter(adapter);
        vm.getListaUsuarios().observe(getViewLifecycleOwner(), usuarios -> {
            adapter.setUsuarios(usuarios);
        });
        vm.cargarInicial();

        // Metodo para buscar
        binding.searchViewUsuario.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        vm.buscar(query);
                        binding.searchViewUsuario.clearFocus();
                        binding.listaUsuarios.scrollToPosition(0);
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty()) {
                            vm.buscar("");
                            binding.listaUsuarios.scrollToPosition(0);
                        }
                        return true;
                    }
                }
        );

        // Scroll infinito
        binding.listaUsuarios.addOnScrollListener(new RecyclerView.OnScrollListener() {
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