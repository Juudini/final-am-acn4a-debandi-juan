package com.example.parcial_2_am_acn4a_debandi_juan;

import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.parcial_2_am_acn4a_debandi_juan.utils.AuthService;

public class SignupActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmInput;
    private Button btnCreate;
    private Button btnHaveAccount;
    private ProgressBar progress;

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

        findViewById(R.id.signup_BtnBack).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        btnCreate.setOnClickListener(v -> submit());
        // Volver a la view signin
        btnHaveAccount.setOnClickListener(v -> finish());
    }

    private void submit() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirm = confirmInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
            Toast.makeText(this, R.string.signup_emptyFields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, R.string.signup_passwordMismatch, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        AuthService.signup(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                Toast.makeText(SignupActivity.this, R.string.signup_success, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(SignupActivity.this,
                        message != null ? message : getString(R.string.signup_error),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnCreate.setEnabled(!loading);
        btnHaveAccount.setEnabled(!loading);
    }
}
