package com.example.qpiqueapp.ui.start;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.qpiqueapp.R;

public class StartFragment extends Fragment {

    public StartFragment() {
        super(R.layout.fragment_start);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Por ahora siempre arranca en Productos
        NavHostFragment.findNavController(this)
                .navigate(R.id.nav_transform);
    }
}
