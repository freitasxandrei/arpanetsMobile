package com.example.arpanetsmobile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class ApiClient {

    private static final String BASE_URL = "http://seu-endereco-api.com/"; // Substitua pelo endereço real da sua API

    // Método para cadastrar o usuário
    public void cadastrarUsuario(String name, String password, String gender, int age) {
        OkHttpClient client = new OkHttpClient();

        // Corpo da requisição em formato JSON
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String json = "{ \"name\": \"" + name + "\", \"password\": \"" + password + "\", \"gender\": \"" + gender + "\", \"age\": " + age + " }";
        RequestBody body = RequestBody.create(mediaType, json);

        // Requisição POST para o endpoint /user
        Request request = new Request.Builder()
                .url(BASE_URL + "user")
                .post(body)
                .build();

        // Enviar a requisição
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();  // Em caso de falha
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // Aqui você pode processar a resposta da API
                }
            }
        });
    }

    // Método para enviar o questionário
    public void enviarQuestionario(int userId, int totalScore, String sufferingLevel) {
        OkHttpClient client = new OkHttpClient();

        // Corpo da requisição para o questionário
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String json = "{ \"user_id\": " + userId + ", \"total_score\": " + totalScore + ", \"suffering_level\": \"" + sufferingLevel + "\" }";
        RequestBody body = RequestBody.create(mediaType, json);

        // Requisição POST para o endpoint /questionnaire
        Request request = new Request.Builder()
                .url(BASE_URL + "questionnaire")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();  // Em caso de falha
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // Aqui você pode processar a resposta da API
                }
            }
        });
    }
}