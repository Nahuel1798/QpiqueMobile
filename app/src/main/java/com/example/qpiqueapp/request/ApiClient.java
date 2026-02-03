package com.example.qpiqueapp.request;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.qpiqueapp.modelo.Categorias;
import com.example.qpiqueapp.modelo.Clientes;
import com.example.qpiqueapp.modelo.ClientesCrearRequest;
import com.example.qpiqueapp.modelo.ClientesResponse;
import com.example.qpiqueapp.modelo.LoginRequest;
import com.example.qpiqueapp.modelo.LoginResponse;
import com.example.qpiqueapp.modelo.PerfilDto;
import com.example.qpiqueapp.modelo.ProductoCrearRequest;
import com.example.qpiqueapp.modelo.Productos;
import com.example.qpiqueapp.modelo.ProductosResponse;
import com.example.qpiqueapp.modelo.RegisterRequest;
import com.example.qpiqueapp.modelo.UsuarioUpdateRequest;
import com.example.qpiqueapp.modelo.UsuariosResponse;
import com.example.qpiqueapp.modelo.VentaCrearRequest;
import com.example.qpiqueapp.modelo.Ventas;
import com.example.qpiqueapp.modelo.VentasPaginadasResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ApiClient {
    public static final String BASE_URL="http://192.168.1.35:5205/";
    // Configurar retrofit
    public static InmoServicio getInmoServicio(){
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(InmoServicio.class);
    }

    public interface InmoServicio {

        // Login
        @POST("api/auth/login")
        Call<LoginResponse> login(@Body LoginRequest loginRequest);

        // Ver perfil
        @GET("api/auth/profile")
        Call<PerfilDto> getProfile(
                @Header("Authorization") String token
        );

        // Registro
        @POST("api/auth/register")
        Call<ResponseBody> crearRegistro(@Body RegisterRequest registerRequest);

        // Editar Perfil
        @Multipart
        @PUT("api/users/me")
        Call<PerfilDto> updateMyProfile(
                @Header("Authorization") String token,
                @Part("Nombre") RequestBody nombre,
                @Part("Apellido") RequestBody apellido,
                @Part("Email") RequestBody email,
                @Part MultipartBody.Part avatar
        );


        // Trae Categorias a productos
        @GET("api/ProductosApi/Categorias")
        Call<List<Categorias>> getCategorias();

        // Trae Productos
        @GET("api/ProductosApi/Filtrados")
        Call<ProductosResponse> getProductos(
                @Query("categoriaId") Integer categoriaId,
                @Query("nombre") String nombre,
                @Query("page") int page,
                @Query("pageSize") int pageSize
        );

        // Crear Producto
        @Multipart
        @POST("api/ProductosApi")
        Call<Productos> crearProducto(
                @Header("Authorization") String token,
                @Part("Nombre") RequestBody nombre,
                @Part("Descripcion") RequestBody descripcion,
                @Part("Precio") RequestBody precio,
                @Part("Stock") RequestBody stock,
                @Part("CategoriaId") RequestBody categoriaId,
                @Part MultipartBody.Part Imagen
        );

        // Editar Producto
        @Multipart
        @PUT("api/ProductosApi/Editar/{id}")
        Call<ResponseBody> editarProducto(
                @Header("Authorization") String token,
                @Path("id") int id,
                @Part("Nombre") RequestBody nombre,
                @Part("Descripcion") RequestBody descripcion,
                @Part("Precio") RequestBody precio,
                @Part("Stock") RequestBody stock,
                @Part("CategoriaId") RequestBody categoriaId,
                @Part MultipartBody.Part nuevaImagen
        );

        // Eliminar Producto
        @DELETE("api/ProductosApi/Eliminar/{id}")
        Call<Void> eliminarProducto(
                @Header("Authorization") String token,
                @Path("id") int id
        );

        // Trae Clientes
        @GET("api/ClientesApi/Paginado")
        Call<ClientesResponse> getClientes(
                @Header("Authorization") String token,
                @Query("page") int page,
                @Query("pageSize") int pageSize,
                @Query("search") String search
        );

        // Crear Cliente
        @POST("api/ClientesApi")
        Call<Clientes> crearCliente(
                @Header("Authorization") String token,
                @Body ClientesCrearRequest cliente
        );

        // Editar Cliente
        @PUT("api/ClientesApi/EditarJson/{id}")
        Call<ResponseBody> editarCliente(
                @Header("Authorization") String token,
                @Path("id") int id,
                @Body Clientes cliente
        );

        // Eliminar Cliente
        @DELETE("api/ClientesApi/Eliminar/{id}")
        Call<Void> eliminarCliente(
                @Header("Authorization") String token,
                @Path("id") int id
        );

        // Trae Categorias
        @GET("api/CategoriasApi")
        Call<List<Categorias>> getCategoria(@Header("Authorization") String token);

        // Editar Categoria
        @Multipart
        @PUT("api/CategoriasApi/{id}")
        Call<ResponseBody> editarCategoria(
                @Header("Authorization") String token,
                @Path("id") int id,
                @Part("id") RequestBody categoriaId,
                @Part("nombre") RequestBody nombre,
                @Part MultipartBody.Part imagen
        );

        // Eliminar Categoria
        @DELETE("api/CategoriasApi/{id}")
        Call<Void> eliminarCategoria(
                @Header("Authorization") String token,
                @Path("id") int id
        );

        // Crear Categoria
        @Multipart
        @POST("api/CategoriasApi")
        Call<Categorias> crearCategoria(
                @Header("Authorization") String token,
                @Part("Nombre") RequestBody nombre,
                @Part MultipartBody.Part imagen
        );

        // Traer Usuario
        @GET("api/users")
        Call<UsuariosResponse> getUsuario(
                @Header("Authorization") String token,
                @Query("page") int page,
                @Query("pageSize") int pageSize,
                @Query("search") String search
        );

        // Por id
        @GET("api/users/{id}")
        Call<PerfilDto> getUsuarioPorId(
                @Header("Authorization") String token,
                @Path("id") String id
        );


        // Editar Usuario
        @PUT("api/users/{id}")
        Call<ResponseBody> editarUsuario(
                @Header("Authorization") String token,
                @Path("id") String id,
                @Body UsuarioUpdateRequest usuario
        );

        // Eliminar Usuario
        @DELETE("api/users/{id}")
        Call<Void> eliminarUsuario(
                @Header("Authorization") String token,
                @Path("id") String id
        );

        // Trae Ventas
        @GET("api/VentasApi/Filtradas")
        Call<VentasPaginadasResponse> getVentas(
                @Header("Authorization") String token,
                @Query("cliente") String cliente,
                @Query("producto") String producto,
                @Query("dia") String dia,
                @Query("fechaDesde") String fechaDesde,
                @Query("fechaHasta") String fechaHasta,
                @Query("page") int page,
                @Query("pageSize") int pageSize
        );

        // Crear Venta
        @POST("api/VentasApi")
        Call<ResponseBody> crearVenta(
                @Header("Authorization") String token,
                @Body VentaCrearRequest venta
        );

        // Editar Venta
        @PUT("api/VentasApi/{id}")
        Call<ResponseBody> editarVenta(
                @Header("Authorization") String token,
                @Path("id") int id,
                @Body VentaCrearRequest venta
        );

        // Eliminar Ventas
        @DELETE("api/VentasApi/{id}")
        Call<Void> eliminarVenta(
                @Header("Authorization") String token,
                @Path("id") int id
        );


    }

    public static void guardartoken(Context context, String token) {
        SharedPreferences prefs =
                context.getSharedPreferences("auth", Context.MODE_PRIVATE);

        prefs.edit()
                .putString("jwt", token)
                .apply();
    }

    public static String leerToken(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences("auth", Context.MODE_PRIVATE);

        return prefs.getString("jwt", null);
    }
}
