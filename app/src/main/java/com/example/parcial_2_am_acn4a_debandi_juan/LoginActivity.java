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

import com.example.parcial_2_am_acn4a_debandi_juan.util.AuthManager;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button btnLogin;
    private Button btnRegister;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.login_Email);
        passwordInput = findViewById(R.id.login_Password);
        btnLogin = findViewById(R.id.login_BtnLogin);
        btnRegister = findViewById(R.id.login_BtnRegister);
        progress = findViewById(R.id.login_Progress);

        findViewById(R.id.login_BtnBack).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        btnLogin.setOnClickListener(v -> submit(false));
        btnRegister.setOnClickListener(v -> submit(true));
    }

    private void submit(boolean register) {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.login_emptyFields, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        AuthManager.AuthCallback callback = new AuthManager.AuthCallback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(LoginActivity.this,
                        message != null ? message : getString(R.string.login_error),
                        Toast.LENGTH_LONG).show();
            }
        };

        if (register) {
            AuthManager.register(email, password, callback);
        } else {
            AuthManager.login(email, password, callback);
        }
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnRegister.setEnabled(!loading);
    }
}
