package com.example.qpiqueapp.ui.ventas;

import static com.example.qpiqueapp.request.ApiClient.BASE_URL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qpiqueapp.R;
import com.example.qpiqueapp.modelo.productos.Productos;
import com.example.qpiqueapp.request.ApiClient;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class SeleccionarProductoAdapter
        extends RecyclerView.Adapter<SeleccionarProductoAdapter.ViewHolder> {

    private List<Productos> listaProductos;
    private List<Productos> seleccionados = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public SeleccionarProductoAdapter(List<Productos> listaProductos, Context context) {
        this.listaProductos = listaProductos;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setProductos(List<Productos> listaProductos) {
        this.listaProductos = listaProductos;
        notifyDataSetChanged();
    }

    // 👉 DEVUELVE LOS SELECCIONADOS
    public List<Productos> getSeleccionados() {
        return seleccionados;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_productos, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Productos producto = listaProductos.get(position);

        holder.tvNombre.setText(producto.getNombre());
        holder.tvDescripcion.setText(producto.getDescripcion());
        holder.tvPrecio.setText("$ " + producto.getPrecio());
        holder.tvStock.setText("Stock: " + producto.getStock());

        String urlImagen = BASE_URL + producto.getImagenUrl();

        Glide.with(holder.itemView.getContext())
                .load(urlImagen)
                .placeholder(R.drawable.ic_settings_black_24dp)
                .error(R.drawable.ic_settings_black_24dp)
                .into(holder.imgPortada);

        // Ocultar botones admin
        holder.btnEditar.setVisibility(View.GONE);
        holder.btnEliminar.setVisibility(View.GONE);

        boolean estaSeleccionado = seleccionados.contains(producto);

        holder.btnAgregar.setText(
                estaSeleccionado ? "Quitar" : "Agregar"
        );

        holder.btnAgregar.setOnClickListener(v -> {
            if (seleccionados.contains(producto)) {
                seleccionados.remove(producto);
            } else {
                seleccionados.add(producto);
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio, tvDescripcion, tvStock;
        ImageView imgPortada, btnEditar, btnEliminar;
        MaterialButton btnAgregar;

        ViewHolder(@NonNull View v) {
            super(v);
            tvNombre = v.findViewById(R.id.tvNombre);
            tvDescripcion = v.findViewById(R.id.tvDescripcion);
            tvPrecio = v.findViewById(R.id.tvPrecio);
            tvStock = v.findViewById(R.id.tvStock);
            imgPortada = v.findViewById(R.id.imgPortada);
            btnEditar = v.findViewById(R.id.btnEditar);
            btnEliminar = v.findViewById(R.id.btnEliminar);
            btnAgregar = v.findViewById(R.id.btnAgregarCarrito);
        }
    }
}
