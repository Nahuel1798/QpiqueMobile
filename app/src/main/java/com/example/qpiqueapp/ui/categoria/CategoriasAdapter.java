package com.example.qpiqueapp.ui.categoria;

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
import com.example.qpiqueapp.modelo.Categorias;
import com.example.qpiqueapp.request.ApiClient;

import java.util.List;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.CategoriasViewHolder> {
    private List<Categorias> listaCategorias;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditar(Categorias categoria);
        void onEliminar(Categorias categoria);
    }

    public CategoriasAdapter(List<Categorias> listaCategorias, Context context, LayoutInflater inflater, OnItemClickListener listener) {
        this.listaCategorias = listaCategorias;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoriasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_categorias, parent, false);
        return new CategoriasViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull CategoriasViewHolder holder, int position) {
        Categorias categoria = listaCategorias.get(position);

        // Configurar los datos en las vistas
        holder.tvNombre.setText(categoria.getNombre());
        // Cargar la imagen desde la URL con Glide
        Glide.with(holder.itemView) // Usar el contexto guardado
                .load(ApiClient.BASE_URL + categoria.getImagenUrl())
                .placeholder(R.drawable.ic_settings_black_24dp)
                .error(R.drawable.ic_settings_black_24dp)
                .into(holder.imgPortada);

        // Boton editar
        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditar(categoria);
            }
        });

        // Boton eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminar(categoria);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCategorias == null ? 0 : listaCategorias.size();
    }

    static class CategoriasViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        ImageView imgPortada;
        ImageView btnEditar;
        ImageView btnEliminar;

        public CategoriasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            imgPortada = itemView.findViewById(R.id.imgPortada);
        }
    }

    public void setCategorias(List<Categorias> listaCategorias) {
        this.listaCategorias = listaCategorias;
        notifyDataSetChanged();
    }
}
