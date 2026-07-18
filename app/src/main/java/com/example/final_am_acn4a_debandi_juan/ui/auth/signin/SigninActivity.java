package com.example.final_am_acn4a_debandi_juan.ui.auth.signin;

import android.content.Intent;
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
import com.example.final_am_acn4a_debandi_juan.ui.auth.signup.SignupActivity;

public class SigninActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private Button btnSignin;
    private Button btnSignup;
    private ProgressBar progress;
    private SigninViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        emailInput = findViewById(R.id.signin_Email);
        passwordInput = findViewById(R.id.signin_Password);
        btnSignin = findViewById(R.id.signin_BtnSignin);
        btnSignup = findViewById(R.id.signin_BtnSignup);
        progress = findViewById(R.id.signin_Progress);

        AppViewModelFactory factory = new AppViewModelFactory(App.getModule(this));
        viewModel = new ViewModelProvider(this, factory).get(SigninViewModel.class);
        viewModel.getState().observe(this, this::renderState);

        findViewById(R.id.signin_BtnBack).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signin_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        btnSignin.setOnClickListener(v -> submit());
        btnSignup.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }

    private void submit() {
        viewModel.signIn(emailInput.getText().toString(), passwordInput.getText().toString());
    }

    private void renderState(SigninUiState state) {
        setLoading(state.isLoading());
        if (state.isSuccess()) {
            Toast.makeText(this, R.string.signin_success, Toast.LENGTH_SHORT).show();
            finish();
        } else if (state.getMessage() != null) {
            Toast.makeText(this, state.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSignin.setEnabled(!loading);
        btnSignup.setEnabled(!loading);
        emailInput.setEnabled(!loading);
        passwordInput.setEnabled(!loading);
    }
}
