package com.example.qpiqueapp.ui.ventas;

import static com.example.qpiqueapp.request.ApiClient.BASE_URL;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qpiqueapp.R;
import com.example.qpiqueapp.databinding.ItemDetalleVentaEditarBinding;
import com.example.qpiqueapp.modelo.venta.DetalleVenta;

import java.util.List;

public class EditarVentaAdapter
        extends RecyclerView.Adapter<EditarVentaAdapter.ViewHolder> {

    public interface OnDetalleChangeListener {
        void onChange();
    }

    private final List<DetalleVenta> detalles;
    private final OnDetalleChangeListener listener;

    public EditarVentaAdapter(
            List<DetalleVenta> detalles,
            OnDetalleChangeListener listener
    ) {
        this.detalles = detalles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ItemDetalleVentaEditarBinding binding =
                ItemDetalleVentaEditarBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        DetalleVenta detalle = detalles.get(position);

        // Texto
        holder.binding.txtProducto.setText(detalle.getProductoNombre());
        holder.binding.txtPrecio.setText("$ " + detalle.getPrecioUnitario());
        holder.binding.tvCantidad.setText(String.valueOf(detalle.getCantidad()));

        // Imagen
        String url = detalle.getImagenUrl() != null
                ? BASE_URL + detalle.getImagenUrl()
                : null;

        Glide.with(holder.itemView)
                .load(url)
                .placeholder(R.drawable.ic_clientes)
                .error(R.drawable.ic_clientes)
                .into(holder.binding.imgProducto);

        // ➖ RESTAR
        holder.binding.btnRestar.setOnClickListener(v -> {
            int cantidad = detalle.getCantidad();
            if (cantidad > 1) {
                cantidad--;
                detalle.setCantidad(cantidad);
                holder.binding.tvCantidad.setText(String.valueOf(cantidad));
                listener.onChange();
            }
        });

        // ➕ SUMAR (control de stock si existe)
        holder.binding.btnSumar.setOnClickListener(v -> {
            int cantidad = detalle.getCantidad();
            int stock = detalle.getStock(); // ⚠️ importante

            if (cantidad < stock) {
                cantidad++;
                detalle.setCantidad(cantidad);
                holder.binding.tvCantidad.setText(String.valueOf(cantidad));
                listener.onChange();
            }
        });

        // 🗑️ ELIMINAR
        holder.binding.btnEliminar.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                detalles.remove(pos);
                notifyItemRemoved(pos);
                listener.onChange();
            }
        });
    }

    @Override
    public int getItemCount() {
        return detalles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemDetalleVentaEditarBinding binding;

        ViewHolder(ItemDetalleVentaEditarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void actualizarLista(List<DetalleVenta> nueva) {
        detalles.clear();
        detalles.addAll(nueva);
        notifyDataSetChanged();
    }
}
