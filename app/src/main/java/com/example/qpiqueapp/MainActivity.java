package com.example.qpiqueapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;

import com.example.qpiqueapp.request.ApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qpiqueapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        if (binding.appBarMain.fab != null) {
            binding.appBarMain.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).setAnchorView(R.id.fab).show());
        }
        // Inicializamos el NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        // Recuperamos el rol desde SharedPreferences
        String rol = ApiClient.leerRol(this);

        // Configuramos Drawer NavigationView
        NavigationView navigationView = binding.navView;
        if (navigationView != null) {
            Menu drawerMenu = navigationView.getMenu();
            if (!rol.equals("Administrador")) {
                drawerMenu.findItem(R.id.nav_settings).setVisible(false);
                drawerMenu.findItem(R.id.usuariosFragment2).setVisible(false);
            }

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow,
                    R.id.nav_settings, R.id.perfilFragment, R.id.usuariosFragment2
            ).setOpenableLayout(binding.drawerLayout).build();

            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        // Configuramos BottomNavigationView
        BottomNavigationView bottomNavigationView = binding.appBarMain.contentMain.bottomNavView;
        if (bottomNavigationView != null) {
            Menu bottomMenu = bottomNavigationView.getMenu();
            if (!rol.equals("Administrador")) {
                bottomMenu.findItem(R.id.nav_settings).setVisible(false);
                bottomMenu.findItem(R.id.usuariosFragment2).setVisible(false);
            }

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow,
                    R.id.nav_settings, R.id.perfilFragment, R.id.usuariosFragment2
            ).build();

            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        // Listener para bloquear navegación directa a fragmentos prohibidos
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (rol.equals("SIN_ROL")) {
                if (destination.getId() != R.id.sinRolFragment) {
                    navController.navigate(R.id.sinRolFragment);
                }
                return;
            }
            if (!rol.equals("Administrador")) {
                int destId = destination.getId();
                if (destId == R.id.nav_settings || destId == R.id.usuariosFragment2) {
                    Toast.makeText(this, "No tienes permisos para acceder", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.nav_transform);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        NavigationView navView = findViewById(R.id.nav_view);
        if (navView == null) {
            getMenuInflater().inflate(R.menu.overflow, menu);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}