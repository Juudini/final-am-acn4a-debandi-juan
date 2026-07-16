package com.example.final_am_acn4a_debandi_juan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.final_am_acn4a_debandi_juan.utils.AuthService;
import com.example.final_am_acn4a_debandi_juan.utils.BottomNavbarHelper;

public class AccountActivity extends AppCompatActivity {

    private TextView emailText;
    private Button btnSignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        emailText = findViewById(R.id.account_Email);
        btnSignout = findViewById(R.id.account_BtnSignout);

        BottomNavbarHelper.setup(this, BottomNavbarHelper.TAB_ACCOUNT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.account_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        btnSignout.setOnClickListener(v -> {
            AuthService.signout();
            Toast.makeText(this, R.string.account_signedOut, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AuthService.isLoggedIn()) {
            finish();
            return;
        }
        bindUser();
    }

    private void bindUser() {
        String email = AuthService.getEmail();
        emailText.setText(email != null ? email : getString(R.string.account_notAvailable));
    }
}
