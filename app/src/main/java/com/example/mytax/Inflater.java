package com.example.mytax;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Inflater {

    ImageButton btnImg;
    private Toolbar toolbar;




    public void info(Context context, int layout) {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View myView = inflater.inflate(layout, null);
        myDialog.setView(myView);
        final AlertDialog dialog = myDialog.create();

        toolbar = myView.findViewById(R.id.toolbar_close);
        toolbar.setTitle("Information");
        btnImg = myView.findViewById(R.id.close);
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
