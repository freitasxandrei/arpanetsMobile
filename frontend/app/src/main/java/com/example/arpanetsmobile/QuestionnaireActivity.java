package com.example.arpanetsmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestionnaireActivity extends AppCompatActivity {

    private RadioGroup[] groups = new RadioGroup[20];

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

        // button send
        Button btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcResult();
            }
        });
    }

    private void calcResult() {
        ApiClient apiClient = new ApiClient();
        List<Boolean> answers = new ArrayList<>();

        for (RadioGroup group : groups) {
            int selectedId = group.getCheckedRadioButtonId();
            if (selectedId == -1) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Responda todas as perguntas antes de enviar!", Toast.LENGTH_SHORT).show()
                );
                return; // sai sem enviar
            }
            String idName = getResources().getResourceEntryName(selectedId);
            answers.add(idName.endsWith("Y")); // true se "Sim"
        }

        // TODO: pegar id real do usaer
        int userId = 1;

        apiClient.enviarQuestionario(userId, answers, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Utils.showErrorDialog(QuestionnaireActivity.this, e.getMessage())
                );
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    // Supondo que backend retorna algo como:
                    // { "totalScore": 12, "sufferingLevel": "str" }
                    try {
                        JSONObject json = new JSONObject(responseData);
                        int totalScore = json.getInt("totalScore");
                        String sufferingLevel = json.getString("sufferingLevel");

                        runOnUiThread(() -> updateFront(totalScore, sufferingLevel));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() ->
                            Utils.showErrorDialog(QuestionnaireActivity.this, "Erro HTTP: " + response.code())
                    );
                }
            }
        });
    }

    private void updateFront(int totalScore, String sufferingLevel) {
        int percentYes = totalScore * 100 / 20;
        int percentNo = 100 - percentYes;

        String resultPercents = "SIM (" + percentYes + "%)  NÃO (" + percentNo + "%)";

        int color;
        if (totalScore <= 7) {
            color = ContextCompat.getColor(this, R.color.result_low);
        } else if (totalScore <= 14) {
            color = ContextCompat.getColor(this, R.color.result_medium);
        } else {
            color = ContextCompat.getColor(this, R.color.result_high);
        }

        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvResult = findViewById(R.id.tvResult);
        TextView tvResultPercents = findViewById(R.id.tvResultPercents);

        tvDescription.setVisibility(View.GONE);
        tvResult.setVisibility(View.VISIBLE);
        tvResultPercents.setVisibility(View.VISIBLE);

        tvResult.setText(sufferingLevel); // recebido do back
        tvResult.setTextColor(color);

        tvResultPercents.setText(resultPercents);

        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_UP));
    }
}