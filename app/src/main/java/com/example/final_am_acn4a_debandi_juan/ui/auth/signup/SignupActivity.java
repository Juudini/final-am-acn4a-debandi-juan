package com.example.final_am_acn4a_debandi_juan.ui.auth.signup;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class SignupActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmInput;
    private Button btnCreate;
    private Button btnHaveAccount;
    private ProgressBar progress;
    private SignupViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        emailInput = findViewById(R.id.signup_Email);
        passwordInput = findViewById(R.id.signup_Password);
        confirmInput = findViewById(R.id.signup_Confirm);
        btnCreate = findViewById(R.id.signup_BtnCreate);
        btnHaveAccount = findViewById(R.id.signup_BtnHaveAccount);
        progress = findViewById(R.id.signup_Progress);

        AppViewModelFactory factory = new AppViewModelFactory(App.getModule(this));
        viewModel = new ViewModelProvider(this, factory).get(SignupViewModel.class);
        viewModel.getState().observe(this, this::renderState);

        findViewById(R.id.signup_BtnBack).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        btnCreate.setOnClickListener(v -> submit());
        btnHaveAccount.setOnClickListener(v -> finish());
    }

    private void submit() {
        viewModel.signUp(
            emailInput.getText().toString(),
            passwordInput.getText().toString(),
            confirmInput.getText().toString()
        );
    }

    private void renderState(SignupUiState state) {
        setLoading(state.isLoading());
        if (state.isSuccess()) {
            Toast.makeText(this, R.string.signup_success, Toast.LENGTH_SHORT).show();
            finish();
        } else if (state.getMessage() != null) {
            Toast.makeText(this, state.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnCreate.setEnabled(!loading);
        btnHaveAccount.setEnabled(!loading);
        emailInput.setEnabled(!loading);
        passwordInput.setEnabled(!loading);
        confirmInput.setEnabled(!loading);
    }
}
