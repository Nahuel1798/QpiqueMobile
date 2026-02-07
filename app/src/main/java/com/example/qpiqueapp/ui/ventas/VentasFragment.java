package com.example.qpiqueapp.ui.ventas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.FragmentVentasBinding;
import com.example.qpiqueapp.modelo.venta.Ventas;

import java.util.ArrayList;
import java.util.Calendar;

public class VentasFragment extends Fragment {

    private FragmentVentasBinding binding;
    private VentasViewModel viewModel;
    private VentasAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVentasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        // ViewModel del Fragment
        viewModel = new ViewModelProvider(this)
                .get(VentasViewModel.class);

        setupRecycler();
        setupObservers();
        setupListeners();

        // carga inicial
        if (viewModel.getVentas().getValue() == null
                || viewModel.getVentas().getValue().isEmpty()) {
            viewModel.cargarVentas();
        }
    }

    private void setupRecycler() {

        adapter = new VentasAdapter(
                new ArrayList<>(),
                getLayoutInflater(),
                new VentasAdapter.OnItemClickListener() {
                    @Override
                    public void onEditar(Ventas ventas) {
                        Bundle b = new Bundle();
                        b.putSerializable("ventas", ventas);
                        NavHostFragment.findNavController(VentasFragment.this)
                                .navigate(R.id.action_nav_reflow_to_editarVentaFragment, b);
                    }

                    @Override
                    public void onEliminar(Ventas ventas) {
                        Bundle b = new Bundle();
                        b.putSerializable("ventas", ventas);
                        NavHostFragment.findNavController(VentasFragment.this)
                                .navigate(R.id.action_nav_reflow_to_borrarVentaFragment, b);
                    }
                }
        );

        layoutManager = new LinearLayoutManager(requireContext());
        binding.rvVentas.setLayoutManager(layoutManager);
        binding.rvVentas.setAdapter(adapter);

        binding.rvVentas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {

                if (dy <= 0) return;

                int visible = layoutManager.getChildCount();
                int total = layoutManager.getItemCount();
                int first = layoutManager.findFirstVisibleItemPosition();

                if (!viewModel.isLoading()
                        && !viewModel.isLastPage()
                        && visible + first >= total - 2) {
                    viewModel.cargarVentas();
                }
            }
        });
    }

    private void setupObservers() {
        viewModel.getVentas().observe(getViewLifecycleOwner(), adapter::setVentas);
    }

    private void setupListeners() {

        binding.searchViewVentas.setOnQueryTextListener(
                new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        viewModel.buscar(query);
                        binding.searchViewVentas.clearFocus();
                        binding.rvVentas.scrollToPosition(0);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String text) {
                        if (text.isEmpty()) {
                            viewModel.buscar(null);
                        }
                        return true;
                    }
                }
        );

        binding.btnFiltrarFecha.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();

            new DatePickerDialog(
                    requireContext(),
                    (view1, y, m, d) ->
                            viewModel.setFechaFiltro(
                                    String.format("%04d-%02d-%02d", y, m + 1, d)
                            ),
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!viewModel.isLoading()) {
            viewModel.recargar();
            binding.rvVentas.scrollToPosition(0);
        }
    }
}
