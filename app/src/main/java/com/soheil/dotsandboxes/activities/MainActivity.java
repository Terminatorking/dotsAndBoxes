package com.soheil.dotsandboxes.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.soheil.dotsandboxes.R;
import com.soheil.dotsandboxes.classes.G;
import com.soheil.dotsandboxes.views.GameView;


public class MainActivity extends AppCompatActivity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnReset =  findViewById(R.id.btn_restart);
        gameView = findViewById(R.id.GameView);

        requestWritePermission();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameView.resetGame();
            }
        });

        gameView.resetGame();
    }


    private void requestWritePermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        G.hasWriteAccess = hasPermission;
        G.createDirectory();
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    G.hasWriteAccess = true;
                    G.createDirectory();
                } else {
                    Toast.makeText(this, "Write to external storage required for loading & saving game", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        gameView.saveGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.loadGame();
    }
}
