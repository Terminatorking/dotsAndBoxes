package com.soheil.dotsandboxes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.soheil.dotsandboxes.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_singlePlayer = findViewById(R.id.btn_singlePlayer);
        Button btn_multiPlayer = findViewById(R.id.btn_multiPlayer);
        Button btn_options = findViewById(R.id.btn_options);
        Button btn_about = findViewById(R.id.btn_about);

        btn_singlePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGame(false);
            }
        });

        btn_multiPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGame(true);
            }
        });

        btn_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void openGame(boolean isMultiplayer) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("isMultiplayer", isMultiplayer);
        MainActivity.this.startActivity(intent);
    }
}
