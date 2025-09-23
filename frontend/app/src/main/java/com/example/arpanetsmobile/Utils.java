package com.example.arpanetsmobile;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class Utils {

    public static void showMessageDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public static void showErrorDialog(Context context, String message) {
        showMessageDialog(context, "Erro", message);
    }

    public static void showSuccessDialog(Context context, String message) {
        showMessageDialog(context, "Sucesso", message);
    }
}