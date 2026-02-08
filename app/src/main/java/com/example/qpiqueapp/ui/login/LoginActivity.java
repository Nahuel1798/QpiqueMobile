package com.example.qpiqueapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.qpiqueapp.MainActivity;
import com.example.qpiqueapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(LoginViewModel.class);

        observarViewModel();
        configurarUI();
    }

    private void observarViewModel() {

        vm.getMensaje().observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );

        vm.getNavegarMain().observe(this, ir -> {
            if (Boolean.TRUE.equals(ir)) {
                Intent intent =
                        new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                );
                startActivity(intent);
            }
        });

        vm.getLoading().observe(this, loading -> {
            binding.loginButton.setEnabled(!loading);
//            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        });
    }

    private void configurarUI() {

        binding.loginButton.setOnClickListener(v ->
                vm.loguear(
                        binding.email.getText().toString(),
                        binding.password.getText().toString()
                )
        );

        binding.registerButton.setOnClickListener(v ->
                startActivity(
                        new Intent(this, RegistroActivity.class)
                )
        );
    }
}

