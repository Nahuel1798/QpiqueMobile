package com.example.qpiqueapp.ui.ventas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qpiqueapp.R;
import com.example.qpiqueapp.modelo.venta.Ventas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VentasAdapter extends RecyclerView.Adapter<VentasAdapter.VentaViewHolder> {
    private final List<Ventas> ventas = new ArrayList<>();
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditar(Ventas ventas);
        void onEliminar(Ventas ventas);
    }

    public VentasAdapter(List<Ventas> ventas, LayoutInflater inflater, OnItemClickListener listener){
        this.ventas.addAll(ventas);
        this.listener = listener;
        this.inflater = inflater;
    }

    public void setVentas(List<Ventas> nuevasVentas) {
        this.ventas.clear();
        this.ventas.addAll(nuevasVentas);
        notifyDataSetChanged();    }
    public void addVentas(List<Ventas> nuevasVentas) {
        if (nuevasVentas == null || nuevasVentas.isEmpty()) return;

        int start = ventas.size();
        ventas.addAll(nuevasVentas);
        notifyItemRangeInserted(start, nuevasVentas.size());
    }

    @NonNull
    @Override
    public VentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ventas, parent, false);
        return new VentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VentaViewHolder holder, int position) {
        Ventas venta = ventas.get(position);

        holder.tvVentaId.setText("Venta #" + venta.getId());
        holder.tvVentaTotal.setText("$ " + venta.getTotal());
        holder.tvVentaFecha.setText(formatearFechaHora(venta.getFecha()));
        holder.tvNombreUsuario.setText("Usuario: " + venta.getUsuarioNombre());
        holder.tvNombreCliente.setText("Cliente: " + venta.getClienteNombre());

        // Cargar detalles de la venta
        holder.detalleAdapter.setDetalles(venta.getDetalleVentas());

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditar(venta);
            }
        });
        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminar(venta);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ventas.size();
    }
    static class VentaViewHolder extends RecyclerView.ViewHolder {

        TextView tvVentaId, tvVentaTotal, tvVentaFecha, tvNombreUsuario, tvNombreCliente;
        RecyclerView rvDetalle;
        DetalleVentaAdapter detalleAdapter;
        Button btnEditar, btnEliminar;


        public VentaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvVentaId = itemView.findViewById(R.id.tvVentaId);
            tvVentaTotal = itemView.findViewById(R.id.tvVentaTotal);
            tvVentaFecha = itemView.findViewById(R.id.tvVentaFecha);
            rvDetalle = itemView.findViewById(R.id.rvDetalleVenta);
            btnEditar = itemView.findViewById(R.id.btnVerDetalle);
            btnEliminar = itemView.findViewById(R.id.btnBorrarVenta);
            tvNombreUsuario = itemView.findViewById(R.id.tvUsuarioNombre);
            tvNombreCliente = itemView.findViewById(R.id.tvClienteNombre);



            // Recycler interno
            detalleAdapter = new DetalleVentaAdapter();
            rvDetalle.setLayoutManager(
                    new LinearLayoutManager(itemView.getContext())
            );
            rvDetalle.setAdapter(detalleAdapter);
            rvDetalle.setNestedScrollingEnabled(false);
            rvDetalle.setHasFixedSize(true);
        }
    }

    // Formatear fecha y hora
    private String formatearFechaHora(String fechaIso) {
        if (fechaIso == null || fechaIso.isEmpty()) return "-";

        try {
            SimpleDateFormat iso = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.getDefault()
            );
            Date date = iso.parse(fechaIso);

            SimpleDateFormat salida = new SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
            );
            return salida.format(date);

        } catch (Exception e) {
            return fechaIso;
        }
    }
}
