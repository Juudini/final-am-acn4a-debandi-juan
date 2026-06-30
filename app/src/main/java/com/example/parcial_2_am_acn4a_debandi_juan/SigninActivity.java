package com.example.parcial_2_am_acn4a_debandi_juan;

import android.content.Intent;
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

public class SigninActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private Button btnSignin;
    private Button btnSignup;
    private ProgressBar progress;

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
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.signin_emptyFields, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        AuthService.signin(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                Toast.makeText(SigninActivity.this, R.string.signin_success, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(SigninActivity.this,
                        message != null ? message : getString(R.string.signin_error),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSignin.setEnabled(!loading);
        btnSignup.setEnabled(!loading);
    }
}
