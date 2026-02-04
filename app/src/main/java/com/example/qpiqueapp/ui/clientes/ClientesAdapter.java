package com.example.qpiqueapp.ui.clientes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qpiqueapp.R;
import com.example.qpiqueapp.modelo.clientes.Clientes;

import java.util.List;

public class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.ClientesViewHolder> {

    private List<Clientes> listaClientes;
    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener listener;
    private OnClienteSeleccionadoListener clienteSeleccionadoListener;
    private boolean modoSeleccion;

    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onEditar(Clientes cliente);
        void onEliminar(Clientes cliente);
    }

    public interface OnClienteSeleccionadoListener {
        void onClienteSeleccionado(Clientes cliente);
    }

    public ClientesAdapter(List<Clientes> listaClientes, Context context, OnItemClickListener listener, OnClienteSeleccionadoListener clienteSeleccionadoListener, boolean modoSeleccion) {
        this.listaClientes = listaClientes;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.clienteSeleccionadoListener = clienteSeleccionadoListener;
        this.modoSeleccion = modoSeleccion;
    }

    @NonNull
    @Override
    public ClientesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_clientes, parent, false);
        return new ClientesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientesViewHolder holder, int position) {
        Clientes cliente = listaClientes.get(position);

        holder.tvNombre.setText(cliente.getNombre());
        holder.tvApellido.setText(cliente.getApellido());
        holder.tvTelefono.setText(cliente.getTelefono());
        holder.tvEmail.setText(cliente.getEmail());

        boolean seleccionado = position == selectedPosition;

        // Logica para mostrar/ocultar botones de seleccion
        if (modoSeleccion) {
            // Mostramos el botón para seleccionar
            holder.btnSeleccionar.setVisibility(View.VISIBLE);
            // Ocultamos los botones de editar y eliminar
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);

            // Asignamos el listener de selección
            holder.btnSeleccionar.setOnClickListener(v -> {
                clienteSeleccionadoListener.onClienteSeleccionado(cliente);
            });

        } else {
            // Si NO estamos en modo selección:
            holder.btnSeleccionar.setVisibility(View.GONE);
            // Mostramos los botones de editar y eliminar
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);

            // Asignamos los listeners de edición/eliminación
            holder.btnEditar.setOnClickListener(v -> listener.onEditar(cliente));
            holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(cliente));
        }
    }

    @Override
    public int getItemCount() {
        return listaClientes == null ? 0 : listaClientes.size();
    }

    static class ClientesViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvApellido, tvTelefono, tvEmail;
        ImageView btnEditar, btnEliminar;
        Button btnSeleccionar;

        public ClientesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvApellido = itemView.findViewById(R.id.tvApellido);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionarCliente);
        }
    }

    public void setClientes(List<Clientes> listaClientes) {
        this.listaClientes = listaClientes;
        selectedPosition = -1;
        notifyDataSetChanged();
    }
}
