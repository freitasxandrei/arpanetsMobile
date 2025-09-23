package com.example.arpanetsmobile;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.List;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/api/"; // Substitua pelo endereço real da sua API

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // Cadastro
    public void registerUser(String name, String password, String gender, int age, Callback callback) {
        // Corpo da requisição em formato JSON
        String json = "{ \"name\": \"" + name + "\", \"password\": \"" + password + "\", \"gender\": \"" + gender + "\", \"age\": " + age + " }";
        RequestBody body = RequestBody.create(json, JSON);

        // Requisição POST para o endpoint /user
        Request request = new Request.Builder()
                .url(BASE_URL + "users/register")
                .post(body)
                .build();

        // Enviar a requisição
        client.newCall(request).enqueue(callback);
    }

    // Login
    public void loginUser(String name, String password, Callback callback) {
        String json = "{ \"name\": \"" + name + "\", \"password\": \"" + password + "\" }";
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + "users/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // Enviar o questionário
    public void submitQuestionnaire(int userId, List<Boolean> answers, okhttp3.Callback callback) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{ \"userId\": ").append(userId).append(", \"answers\": [");

        for (int i = 0; i < answers.size(); i++) {
            jsonBuilder.append(answers.get(i));
            if (i < answers.size() - 1) {
                jsonBuilder.append(", ");
            }
        }
        jsonBuilder.append("] }");

        RequestBody body = RequestBody.create(jsonBuilder.toString(), JSON);

        // Requisição POST para o endpoint /questionnaire
        Request request = new Request.Builder()
                .url(BASE_URL + "questionnaires")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }
}