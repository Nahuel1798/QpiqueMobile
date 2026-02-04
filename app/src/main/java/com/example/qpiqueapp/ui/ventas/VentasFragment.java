package com.example.qpiqueapp.ui.ventas;

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
import com.example.qpiqueapp.modelo.venta.Ventas;

import java.util.ArrayList;

public class VentasFragment extends Fragment {

    private VentasViewModel viewModel;
    private VentasAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ventas, container, false);

        RecyclerView rvVentas = view.findViewById(R.id.rvVentas);

        adapter = new VentasAdapter(
                new ArrayList<>(),
                getLayoutInflater(),
                new VentasAdapter.OnItemClickListener() {
                    @Override
                    public void onEditar(Ventas ventas) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("ventas", ventas); // nombre coincide con nav_graph
                        NavHostFragment.findNavController(VentasFragment.this)
                                .navigate(R.id.action_nav_reflow_to_editarVentaFragment, bundle);
                    }

                    @Override
                    public void onEliminar(Ventas ventas) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("ventas", ventas); // nombre coincide con nav_graph
                        NavHostFragment.findNavController(VentasFragment.this)
                                .navigate(R.id.action_nav_reflow_to_borrarVentaFragment, bundle);
                    }
                }
        );

        layoutManager = new LinearLayoutManager(getContext());

        rvVentas.setLayoutManager(layoutManager);
        rvVentas.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(VentasViewModel.class);

        // OBSERVER
        viewModel.getVentas().observe(getViewLifecycleOwner(), ventas -> {
            adapter.addVentas(ventas);
        });

        // SCROLL INFINITO
        rvVentas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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

        // PRIMERA CARGA
        viewModel.cargarVentas();

        return view;
    }
}
