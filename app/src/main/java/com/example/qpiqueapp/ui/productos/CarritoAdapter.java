package com.example.qpiqueapp.ui.productos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qpiqueapp.R;
import com.example.qpiqueapp.modelo.productos.Productos;
import com.example.qpiqueapp.request.ApiClient;

import java.util.List;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {

    private List<Productos> lista;
    private OnEliminarClick listener;

    public interface OnEliminarClick {
        void onEliminar(Productos producto);
        void onCantidadCambiada();
    }

    public CarritoAdapter(List<Productos> lista, OnEliminarClick listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public void setLista(List<Productos> lista) {
        this.lista = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carrito, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Productos pro = lista.get(position);

        holder.nombre.setText(pro.getNombre());
        holder.cantidad.setText(String.valueOf(pro.getCantidad()));
        holder.stock.setText("Stock: " + pro.getStock());

        String url = pro.getImagenUrl();
        if (url != null && !url.startsWith("http")) url = ApiClient.BASE_URL + url;

        Glide.with(holder.itemView.getContext())
                .load(url)
                .placeholder(R.drawable.ic_settings_black_24dp)
                .error(R.drawable.ic_settings_black_24dp)
                .into(holder.imagen);

        if (listener != null) {
            holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(pro));

            holder.btnSumar.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Productos p = lista.get(pos);
                    if (p.getCantidad() < p.getStock()) {
                        p.setCantidad(p.getCantidad() + 1);
                        notifyItemChanged(pos);
                        listener.onCantidadCambiada();
                    }
                }
            });

            holder.btnRestar.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Productos p = lista.get(pos);
                    if (p.getCantidad() > 1) {
                        p.setCantidad(p.getCantidad() - 1);
                        notifyItemChanged(pos);
                        listener.onCantidadCambiada();
                    }
                }
            });
        } else {
            holder.btnEliminar.setVisibility(View.GONE);
            holder.btnSumar.setVisibility(View.GONE);
            holder.btnRestar.setVisibility(View.GONE);
        }

        holder.btnRestar.setEnabled(pro.getCantidad() > 1);
        holder.btnSumar.setEnabled(pro.getCantidad() < pro.getStock());

        double subtotal = pro.getPrecio() * pro.getCantidad();
        holder.precio.setText("$ " + String.format("%.2f", subtotal));
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, precio, cantidad, stock;
        ImageButton btnEliminar, btnSumar, btnRestar;
        ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombreProducto);
            precio = itemView.findViewById(R.id.tvPrecioUnitario);
            cantidad = itemView.findViewById(R.id.tvCantidad);
            stock = itemView.findViewById(R.id.tvStock);
            btnEliminar = itemView.findViewById(R.id.btnEliminarItem);
            btnSumar = itemView.findViewById(R.id.btnSumar);
            btnRestar = itemView.findViewById(R.id.btnRestar);
            imagen = itemView.findViewById(R.id.imgProducto);
        }
    }
}
