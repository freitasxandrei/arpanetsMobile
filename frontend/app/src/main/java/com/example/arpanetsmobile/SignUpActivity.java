package com.example.arpanetsmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput, ageInput;
    private Spinner genderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        ageInput = findViewById(R.id.age);
        genderSpinner = findViewById(R.id.spinnerGender);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        Button btnBack = findViewById(R.id.btnBack);

        // opções do Spinner (gênero)
        String[] genders = {"Masculino", "Feminino", "Outro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        // botão de cadastro
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        // botão voltar
        btnBack.setOnClickListener(v -> finish());
    }

    private void signUp() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);

        ApiClient apiClient = new ApiClient();
        apiClient.registerUser(username, password, gender, age, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Utils.showErrorDialog(SignUpActivity.this, e.getMessage())
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Cadastro realizado!", Toast.LENGTH_SHORT).show();
                        // volta para a tela de login
                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Erro no cadastro", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}