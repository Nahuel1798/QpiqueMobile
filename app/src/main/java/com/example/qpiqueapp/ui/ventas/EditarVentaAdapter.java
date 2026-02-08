package com.example.qpiqueapp.ui.ventas;

import static android.content.ContentValues.TAG;
import static com.example.qpiqueapp.request.ApiClient.BASE_URL;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

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
        void onCantidadChange();
        void onEliminar(DetalleVenta detalle);
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

        holder.binding.txtProducto.setText(detalle.getProductoNombre());
        holder.binding.txtPrecio.setText("$ " + detalle.getPrecioUnitario());
        holder.binding.tvCantidad.setText(String.valueOf(detalle.getCantidad()));

        String url = detalle.getImagenUrl() != null
                ? BASE_URL + detalle.getImagenUrl()
                : null;

        Glide.with(holder.itemView)
                .load(url)
                .placeholder(R.drawable.ic_clientes)
                .error(R.drawable.ic_clientes)
                .into(holder.binding.imgProducto);

        // Restar
        holder.binding.btnRestar.setOnClickListener(v -> {
            int cantidad = detalle.getCantidad();

            if (cantidad > 1) {
                detalle.setCantidad(cantidad - 1);
                holder.binding.tvCantidad.setText(
                        String.valueOf(detalle.getCantidad())
                );
                listener.onCantidadChange();
            }
        });

        // Sumar con control de stock
        holder.binding.btnSumar.setOnClickListener(v -> {
            int cantidadActual = detalle.getCantidad();
            int stock = detalle.getStock();
            int original = detalle.getCantidadOriginal();

            int stockEditable = stock + original;

            // No me llegaba bien el stock(problema solucionado no mandaba el stock en la api)

            Log.d(TAG,
                    "SUMAR -> Producto: " + detalle.getProductoNombre()
                            + " | StockBD=" + stock
                            + " | Original=" + original
                            + " | Actual=" + cantidadActual
                            + " | Editable=" + stockEditable
            );

            if (cantidadActual < stockEditable) {
                detalle.setCantidad(cantidadActual + 1);
                holder.binding.tvCantidad.setText(
                        String.valueOf(detalle.getCantidad())
                );
                listener.onCantidadChange();
            } else {
                Toast.makeText(
                        holder.itemView.getContext(),
                        "No hay más stock disponible",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // Eliminar
        holder.binding.btnEliminar.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onEliminar(detalles.get(pos));
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
