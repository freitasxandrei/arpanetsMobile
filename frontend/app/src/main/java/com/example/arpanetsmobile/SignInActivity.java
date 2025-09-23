package com.example.arpanetsmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

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

        // se existir, pega o username vindo do SignUpActivity
        String prefillUsername = getIntent().getStringExtra("username");
        if (prefillUsername != null) {
            usernameInput.setText(prefillUsername);
        }

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
            Utils.showToast(this, "Preencha usuário e senha!");
            return;
        }

        ApiClient apiClient = new ApiClient();
        apiClient.loginUser(username, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Utils.showToast(SignInActivity.this, "Erro de rede")
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";

                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        int userId = json.getInt("id");
                        String username = json.getString("name");

                        runOnUiThread(() -> {
                            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("userId", userId);
                            editor.putString("username", username);
                            editor.apply();

                            Utils.showToast(SignInActivity.this, "Login bem-sucedido!");
                            // vai para tela de questionario
                            startActivity(new Intent(SignInActivity.this, QuestionnaireActivity.class));
                            finish();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> {
                        Utils.showToast(SignInActivity.this, "Erro HTTP: " + response.code() + " - " + responseBody);
                    });
                }
            }
        });
    }
}