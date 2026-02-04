package com.example.qpiqueapp.ui.usuarios;

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
import com.example.qpiqueapp.modelo.perfil.PerfilDto;

import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuariosViewHolder> {
    private List<PerfilDto> listaUsuarios;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditar(PerfilDto usuario);
        void onEliminar(PerfilDto usuario);
    }

    public UsuariosAdapter(List<PerfilDto> listaUsuarios, Context context, LayoutInflater inflater, OnItemClickListener listener) {
        this.listaUsuarios = listaUsuarios;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuariosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_usuarios, parent, false);
        return new UsuariosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariosViewHolder holder, int position) {
        PerfilDto usuario = listaUsuarios.get(position);

        holder.tvNombre.setText(usuario.getNombre());
        holder.tvApellido.setText(usuario.getApellido());
        holder.tvEmail.setText(usuario.getEmail());
        holder.tvRoles.setText(String.join(", ", usuario.getRoles()));

        String baseUrl = BASE_URL;
        String urlAvatar = usuario.getAvatar() != null ? baseUrl + usuario.getAvatar() : null;

        Glide.with(holder.itemView)
                .load(urlAvatar)
                .placeholder(R.drawable.ic_clientes)
                .error(R.drawable.ic_clientes)
                .into(holder.imgAvatar);
        holder.btnEditar.setOnClickListener(v -> listener.onEditar(usuario));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(usuario));
    }

    @Override
    public int getItemCount() {
        return listaUsuarios == null ? 0 : listaUsuarios.size();
    }

    static class UsuariosViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvApellido, tvEmail, tvRoles;
        ImageView btnEditar, btnEliminar;
        ImageView imgAvatar;

        public UsuariosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvApellido = itemView.findViewById(R.id.tvApellido);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            tvRoles = itemView.findViewById(R.id.tvRoles);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
    public void setUsuarios(List<PerfilDto> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
        notifyDataSetChanged();
    }
}
