package com.example.qpiqueapp.ui.productos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qpiqueapp.R;
import com.example.qpiqueapp.modelo.productos.Productos;
import com.example.qpiqueapp.request.ApiClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductosViewHolder> {

    private List<Productos> listaProductos;
    private Context context;
    private LayoutInflater inflater;
    private final Set<Integer> productosAgregadosIds = new HashSet<>();
    private OnItemClickListener listener;
    private boolean esAdmin;

    public interface OnItemClickListener {
        void onEditar(Productos producto);
        void onEliminar(Productos producto);
        void onAgregarCarrito(Productos producto);
        void onQuitarCarrito(Productos producto);
    }

    public ProductosAdapter(List<Productos> listaProductos, Context context, LayoutInflater inflater, OnItemClickListener listener) {
        this.listaProductos = listaProductos;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;

        String rol = ApiClient.leerRol(context);
        this.esAdmin = rol != null && rol.equals("Administrador");
    }


    @NonNull
    @Override
    public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_productos, parent, false);
        return new ProductosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosViewHolder holder, int position) {
        Productos producto = listaProductos.get(position);

        // Configurar los datos en las vistas
        holder.tvNombre.setText(producto.getNombre());
        holder.tvDescripcion.setText(producto.getDescripcion());
        // Formatear el precio
        String precioFormateado = String.format("$ %.2f", producto.getPrecio());
        holder.tvPrecio.setText(precioFormateado);
        // Formatear el stock
        String stockFormateado = String.format("Stock: %d", producto.getStock());
        holder.tvStock.setText(stockFormateado);
        // Cargar la imagen desde la URL con Glide
        Glide.with(holder.itemView) // Usar el contexto guardado
                .load(ApiClient.BASE_URL + producto.getImagenUrl())
                .placeholder(R.drawable.ic_settings_black_24dp)
                .error(R.drawable.ic_settings_black_24dp)
                .into(holder.imgPortada);

        actualizarBotonCarrito(holder.btnAgregarCarrito, producto);
        holder.btnAgregarCarrito.setOnClickListener(v -> {
            if (listener != null) {
                if (productosAgregadosIds.contains(producto.getId())) {
                    listener.onQuitarCarrito(producto);
                    // Actualizamos nuestro set local
                    productosAgregadosIds.remove(producto.getId());
                } else {
                    listener.onAgregarCarrito(producto);
                    // Actualizamos nuestro set local
                    productosAgregadosIds.add(producto.getId());
                }
                // Actualiza la apariencia del botón inmediatamente después del clic
                actualizarBotonCarrito(holder.btnAgregarCarrito, producto);
            }
        });
        if (esAdmin) {
            holder.btnEliminar.setVisibility(View.VISIBLE);
        } else {
            holder.btnEliminar.setVisibility(View.GONE);
        }
        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditar(producto);
            }
        });
        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminar(producto);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos == null ? 0 : listaProductos.size();
    }

    private void actualizarBotonCarrito(Button boton, Productos producto) {
        if (productosAgregadosIds.contains(producto.getId())) {
            boton.setText("Quitar");
            // Opcional: Cambiar ícono. Asegúrate de tener ic_remove_shopping_cart en res/drawable
            boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_shopping_cart, 0, 0, 0);
        } else {
            // El producto NO está en el carrito: configurar para "Añadir"
            boton.setText("Añadir");
            // Opcional: Cambiar ícono. Asegúrate de tener ic_add_shopping_cart en res/drawable
            boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_shopping_cart, 0, 0, 0);
        }
    }

    static class ProductosViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvPrecio;
        TextView tvDescripcion;
        TextView tvStock;
        ImageView imgPortada;
        ImageView btnEditar;
        ImageView btnEliminar;
        Button btnAgregarCarrito;



        public ProductosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvStock = itemView.findViewById(R.id.tvStock);
            imgPortada = itemView.findViewById(R.id.imgPortada);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnAgregarCarrito = itemView.findViewById(R.id.btnAgregarCarrito);
        }
    }

    public void setProductos(List<Productos> listaProductos) {
        this.listaProductos = listaProductos;
        notifyDataSetChanged();
    }

    public void setProductosAgregados(Set<Integer> ids) {
        this.productosAgregadosIds.clear();
        if (ids != null) {
            this.productosAgregadosIds.addAll(ids);
        }
        notifyDataSetChanged(); // Para que el RecyclerView redibuje los items con el estado correcto del botón.
    }

}
