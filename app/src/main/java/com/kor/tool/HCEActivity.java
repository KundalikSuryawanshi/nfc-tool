package com.kor.tool;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HCEActivity extends AppCompatActivity {

    public static final String PREF_NAME = "hce_card_data";
    public static final String KEY_CARD_DATA = "card_data";

    private EditText cardDataInput;
    private TextView statusText;
    private Button saveButton;
    private Button sampleLinkButton;
    private Button samplePhoneButton;
    private Button sampleTextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hceactivity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardDataInput = findViewById(R.id.cardDataInput);
        statusText = findViewById(R.id.statusText);
        saveButton = findViewById(R.id.saveButton);
        sampleLinkButton = findViewById(R.id.sampleLinkButton);
        samplePhoneButton = findViewById(R.id.samplePhoneButton);
        sampleTextButton = findViewById(R.id.sampleTextButton);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String saved = prefs.getString(KEY_CARD_DATA, "https://example.com");
        cardDataInput.setText(saved);

        saveButton.setOnClickListener(v -> saveCardData());

        sampleLinkButton.setOnClickListener(v -> {
            cardDataInput.setText("https://example.com");
            saveCardData();
        });

        samplePhoneButton.setOnClickListener(v -> {
            cardDataInput.setText("tel:+919999999999");
            saveCardData();
        });

        sampleTextButton.setOnClickListener(v -> {
            cardDataInput.setText("Hello from Android HCE");
            saveCardData();
        });
    }

    private void saveCardData() {
        String data = cardDataInput.getText().toString().trim();

        if (data.isEmpty()) {
            statusText.setText("Enter card data first.");
            return;
        }

        getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_CARD_DATA, data)
                .apply();

        statusText.setText("Saved. Keep phone unlocked and tap reader phone.");
    }
}