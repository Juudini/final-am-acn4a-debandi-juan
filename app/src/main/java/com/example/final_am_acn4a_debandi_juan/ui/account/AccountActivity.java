package com.example.final_am_acn4a_debandi_juan.ui.account;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.final_am_acn4a_debandi_juan.App;
import com.example.final_am_acn4a_debandi_juan.R;
import com.example.final_am_acn4a_debandi_juan.di.AppViewModelFactory;
import com.example.final_am_acn4a_debandi_juan.utils.BottomNavbarHelper;

public class AccountActivity extends AppCompatActivity {
    private TextView emailText;
    private Button btnSignout;
    private AccountViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        emailText = findViewById(R.id.account_Email);
        btnSignout = findViewById(R.id.account_BtnSignout);

        AppViewModelFactory factory = new AppViewModelFactory(App.getModule(this));
        viewModel = new ViewModelProvider(this, factory).get(AccountViewModel.class);
        viewModel.getState().observe(this, this::renderState);

        BottomNavbarHelper.setup(this, BottomNavbarHelper.TAB_ACCOUNT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.account_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        btnSignout.setOnClickListener(v -> {
            Toast.makeText(this, R.string.account_signedOut, Toast.LENGTH_SHORT).show();
            viewModel.signOut();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.refresh();
    }

    private void renderState(AccountUiState state) {
        if (!state.isLoggedIn()) {
            finish();
            return;
        }

        String email = state.getEmail();
        emailText.setText(email != null ? email : getString(R.string.account_notAvailable));
    }
}
