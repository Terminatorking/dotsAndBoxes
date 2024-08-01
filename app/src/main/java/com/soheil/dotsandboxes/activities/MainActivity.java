package com.soheil.dotsandboxes.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.soheil.dotsandboxes.R;
import com.soheil.dotsandboxes.views.GameView;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_restart = findViewById(R.id.btn_restart);
        GameView gameView = findViewById(R.id.GameView);
        btn_restart.setOnClickListener(view -> {
            gameView.restartGame();
        });
    }
}