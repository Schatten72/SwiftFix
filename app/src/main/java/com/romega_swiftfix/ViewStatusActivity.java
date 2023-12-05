package com.romega_swiftfix;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ViewStatusActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardViewStatus;
    private TextView textViewStatusPopupTrigger;
    private RelativeLayout relativeLayoutStatusPopup;
    private TextView textViewClosePopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);

        // Initialize views
        cardViewStatus = findViewById(R.id.cardViewStatus);
        textViewStatusPopupTrigger = findViewById(R.id.textViewStatusPopupTrigger);
        relativeLayoutStatusPopup = findViewById(R.id.relativeLayoutStatusPopup);
        textViewClosePopup = findViewById(R.id.textViewClosePopup);

        // Set click listeners
        textViewStatusPopupTrigger.setOnClickListener(this);
        textViewClosePopup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textViewStatusPopupTrigger) {
            // Show detailed status popup
            relativeLayoutStatusPopup.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.textViewClosePopup) {
            // Close detailed status popup
            relativeLayoutStatusPopup.setVisibility(View.GONE);
        }
    }


}
