package com.example.buysellrent.Class;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.buysellrent.R;

public class ProgressBar {
    private Activity activity;
    private AlertDialog dialog;

    public ProgressBar(Activity activity) {
        this.activity = activity;
    }

    public void loadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyTheme);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.custom_progress, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

}
