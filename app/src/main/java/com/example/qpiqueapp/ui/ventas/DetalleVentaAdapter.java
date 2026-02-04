package com.example.qpiqueapp.ui.ventas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qpiqueapp.R;
import com.example.qpiqueapp.modelo.venta.DetalleVenta;
import com.example.qpiqueapp.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class DetalleVentaAdapter extends RecyclerView.Adapter<DetalleVentaAdapter.DetalleViewHolder> {

    private List<DetalleVenta> detalles = new ArrayList<>();
    public DetalleVentaAdapter() {

    }

    public void setDetalles(List<DetalleVenta> nuevosDetalles) {
        this.detalles.clear();

        if (nuevosDetalles != null) {
            this.detalles.addAll(nuevosDetalles);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetalleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detalleventa, parent, false);
        return new DetalleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetalleViewHolder holder, int position) {
        DetalleVenta d = detalles.get(position);

        holder.tvNombre.setText(d.getProductoNombre());
        holder.tvCantidadPrecio.setText(
                d.getCantidad() + " x $" + d.getPrecioUnitario()
        );

        double subtotal = d.getCantidad() * d.getPrecioUnitario();
        holder.tvSubtotal.setText("$" + subtotal);

        Glide.with(holder.itemView)
                .load(ApiClient.BASE_URL + d.getImagenUrl())
                .placeholder(R.drawable.ic_settings_black_24dp)
                .error(R.drawable.ic_settings_black_24dp)
                .into(holder.imgProducto);
    }

    @Override
    public int getItemCount() {
        return detalles.size();
    }

    static class DetalleViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView tvNombre, tvCantidadPrecio, tvSubtotal;

        public DetalleViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProducto);
            tvNombre = itemView.findViewById(R.id.tvProductoNombre);
            tvCantidadPrecio = itemView.findViewById(R.id.tvCantidadPrecio);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }
}
