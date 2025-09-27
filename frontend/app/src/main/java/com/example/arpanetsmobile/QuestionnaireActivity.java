package com.example.arpanetsmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class QuestionnaireActivity extends AppCompatActivity {

    private final RadioGroup[] groups = new RadioGroup[20];
    private MaterialButton btnSend;
    private CircularProgressIndicator btnProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire);

        // mapear os RadioGroups (Q1 a Q20)
        for (int i = 0; i < groups.length; i++) {
            String rgId = "rgQ" + (i + 1);
            int resId = getResources().getIdentifier(rgId, "id", getPackageName());
            groups[i] = findViewById(resId);
        }

        btnSend = findViewById(R.id.btnSend);
        btnProgress = findViewById(R.id.btnProgress);

        // button send
        btnSend.setOnClickListener(v -> calcResult());
    }

    private void setLoading(boolean loading) {
        runOnUiThread(() -> {
            btnSend.setEnabled(!loading);
            btnSend.setText(loading ? "" : "Enviar"); // some com o texto
            btnProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
        });
    }

    private void calcResult() {
        ApiClient apiClient = new ApiClient();
        List<Boolean> answers = new ArrayList<>();

        for (RadioGroup group : groups) {
            int selectedId = group.getCheckedRadioButtonId();
            if (selectedId == -1) {
                runOnUiThread(() ->
                        Utils.showToast(this, getString(R.string.missing_answers))
                );
                return; // sai sem enviar
            }
            String idName = getResources().getResourceEntryName(selectedId);
            answers.add(idName.endsWith("Y")); // true se "Sim"
        }

        // pegar id do usaer
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        // ativa loading
        setLoading(true);

        apiClient.submitQuestionnaire(userId, answers, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    setLoading(false);
                    Utils.showToast(QuestionnaireActivity.this, "Erro de rede");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (Response r = response) {
                    String responseBody = r.body() != null ? response.body().string() : "";

                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(responseBody);
                            int totalScore = json.getInt("totalScore");
                            String sufferingLevel = json.getString("sufferingLevel");
                            String responseDate = json.getString("responseDate");

                            boolean[] answersArray = new boolean[answers.size()];
                            for (int i = 0; i < answers.size(); i++) {
                                answersArray[i] = answers.get(i);
                            }

                            Intent intent = new Intent(QuestionnaireActivity.this, ResultActivity.class);
                            intent.putExtra("totalScore", totalScore);
                            intent.putExtra("sufferingLevel", sufferingLevel);
                            intent.putExtra("responseDate", responseDate);
                            intent.putExtra("answers", answersArray);

                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == 404) {
                        runOnUiThread(() ->
                                Utils.showToast(QuestionnaireActivity.this, getString(R.string.user_not_found))
                        );
                    } else {
                        runOnUiThread(() ->
                                Utils.showToast(QuestionnaireActivity.this, "Erro HTTP: " + response.code() + responseBody)
                        );
                    }

                    setLoading(false);
                }
            }
        });
    }
}