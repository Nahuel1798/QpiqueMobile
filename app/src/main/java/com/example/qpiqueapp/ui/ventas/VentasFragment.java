package com.example.qpiqueapp.ui.ventas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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

        adapter = new VentasAdapter(
                new ArrayList<>(),
                getLayoutInflater(),
                new VentasAdapter.OnItemClickListener() {
                    @Override
                    public void onEditar(Ventas ventas) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("ventas", ventas);
                        NavHostFragment.findNavController(VentasFragment.this)
                                .navigate(R.id.action_nav_reflow_to_editarVentaFragment, bundle);
                    }

                    @Override
                    public void onEliminar(Ventas ventas) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("ventas", ventas);
                        NavHostFragment.findNavController(VentasFragment.this)
                                .navigate(R.id.action_nav_reflow_to_borrarVentaFragment, bundle);
                    }
                }
        );

        layoutManager = new LinearLayoutManager(getContext());
        binding.rvVentas.setLayoutManager(layoutManager);
        binding.rvVentas.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(VentasViewModel.class);

        // Observa ventas
        viewModel.getVentas().observe(getViewLifecycleOwner(), ventas -> adapter.setVentas(ventas));

        // Scroll infinito
        binding.rvVentas.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy <= 0) return;

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!viewModel.isLoading()
                        && !viewModel.isLastPage()
                        && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                    viewModel.cargarVentas();
                }
            }
        });

        // Buscar por producto solo al apretar Enter
        binding.searchViewVentas.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.buscar(query.trim());
                binding.searchViewVentas.clearFocus();
                binding.rvVentas.scrollToPosition(0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    viewModel.buscar("");
                    binding.rvVentas.scrollToPosition(0);
                }
                return true;
            }
        });

        // Filtrar por fecha
        binding.btnFiltrarFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(requireContext(), (DatePicker view1, int y, int m, int d) -> {
                String fechaSeleccionada = String.format("%04d-%02d-%02d", y, m + 1, d);
                viewModel.setFechaFiltro(fechaSeleccionada);
            }, year, month, day);

            dpd.show();
        });

        // Primera carga
        viewModel.cargarVentas();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
