package com.example.arpanetsmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        int totalScore = getIntent().getIntExtra("totalScore", 0);
        String sufferingLevel = getIntent().getStringExtra("sufferingLevel");
        String responseDate = getIntent().getStringExtra("responseDate");
        boolean[] answersArray = getIntent().getBooleanArrayExtra("answers");

        // atualiza a parte principal
        updateFront(totalScore, sufferingLevel, responseDate);

        // faz a lista de respostas
        if (answersArray != null) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < answersArray.length; i++) {
                sb.append(getString(R.string.label_question_number))
                        .append(i + 1)
                        .append(": ")
                        .append(answersArray[i] ? getString(R.string.answer_yes) : getString(R.string.answer_no))
                        .append("\n");
            }

            TextView tvAnswers = findViewById(R.id.tvAnswers);
            tvAnswers.setText(sb.toString());
        }

        // botao refazer questionario
        findViewById(R.id.btnQuestionnaire).setOnClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, QuestionnaireActivity.class));
            finish();
        });
    }

    private void updateFront(int totalScore, String sufferingLevel, String responseDate) {
        String formattedDate = "";

        try {
            LocalDateTime ldt = LocalDateTime.parse(responseDate);

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault());

            formattedDate = ldt.format(outputFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            formattedDate = responseDate; // fallback: se não der mostra sem formatar
        }

        int percentYes = totalScore * 100 / 20;
        int percentNo = 100 - percentYes;

        String scoreText = String.format(Locale.getDefault(), "%d/20", totalScore);

        String resultPercents = String.format(Locale.getDefault(), "SIM (%d%%)  NÃO (%d%%)", percentYes, percentNo);

        int color;
        if (totalScore <= 7) {
            color = ContextCompat.getColor(this, R.color.result_low);
        } else if (totalScore <= 14) {
            color = ContextCompat.getColor(this, R.color.result_medium);
        } else {
            color = ContextCompat.getColor(this, R.color.result_high);
        }

        TextView tvResult = findViewById(R.id.tvResult);
        TextView tvResultAnswers = findViewById(R.id.tvResultAnswers);
        TextView tvResultPercents = findViewById(R.id.tvResultPercents);
        TextView tvResultDate = findViewById(R.id.tvResultDate);

        tvResult.setText(sufferingLevel); // recebido do back
        tvResult.setTextColor(color);

        tvResultAnswers.setText(scoreText);

        tvResultPercents.setText(resultPercents);

        tvResultDate.setText(formattedDate);
    }
}