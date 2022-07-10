package com.apnagroup.apnavpn.browser.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.apnagroup.apnavpn.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText search_bar;
    private FloatingActionButton button;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_mainbrowser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarold);
        toolbar.setTitle("Secure Browser");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        search_bar = findViewById(R.id.search_bar);
        button = findViewById(R.id.news);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("page_url", "https://news.google.com/");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        button = findViewById(R.id.search_voice_btn);
        button.setOnClickListener(v -> promptSpeechInput());
        button = findViewById(R.id.bookmarks);
        button.setOnClickListener(v -> {
            Intent activitySetIntent = new Intent(getApplicationContext(), BookmarksActivity.class);
            startActivity(activitySetIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        button = findViewById(R.id.map);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("page_url", "https://maps.google.com/");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        button = findViewById(R.id.translate);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("page_url", "https://translate.google.com/");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        button = findViewById(R.id.settings);
        button.setOnClickListener(v -> {
            Intent activitySetIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(activitySetIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
       search_bar.setOnEditorActionListener((v, actionId, event) -> {
           if (actionId == EditorInfo.IME_ACTION_SEARCH) {
               performSearch();
               return true;
           }
           return false;
       });
    }
    private void performSearch() {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("text", search_bar.getText().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    search_bar.setText(result.get(0));
                    Intent intent = new Intent(this, WebActivity.class);
                    intent.putExtra("text", search_bar.getText().toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            }

        }
    }

}
