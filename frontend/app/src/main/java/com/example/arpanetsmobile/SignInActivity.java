package com.example.arpanetsmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignInActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnSignUp = findViewById(R.id.btnSignUp);

        // button sign in
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // button sign up
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void signIn() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha usuário e senha!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient apiClient = new ApiClient();
        apiClient.loginUser(username, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Utils.showErrorDialog(SignInActivity.this, e.getMessage())
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(SignInActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                        // vai para tela de questionario
                        startActivity(new Intent(SignInActivity.this, QuestionnaireActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}