package com.soheil.dotsandboxes.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.soheil.dotsandboxes.R;
import com.soheil.dotsandboxes.classes.EnhancedActivity;
import com.soheil.dotsandboxes.classes.G;


public class MainActivity extends EnhancedActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    requestWritePermission();

    Button btn_resume = findViewById(R.id.btn_resume);
    Button btn_singlePlayer = findViewById(R.id.btn_singlePlayer);
    Button btn_multiPlayer = findViewById(R.id.btn_multiPlayer);
    Button btn_options = findViewById(R.id.btn_options);
    Button btn_about = findViewById(R.id.btn_about);

    btn_resume.setOnClickListener(view -> resumeGame());

    btn_singlePlayer.setOnClickListener(view -> openGame(false));

    btn_multiPlayer.setOnClickListener(view -> openGame(true));

    btn_options.setOnClickListener(view -> {
      Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
      MainActivity.this.startActivity(intent);
    });

    btn_about.setOnClickListener(view -> {
      Intent intent = new Intent(MainActivity.this, AboutActivity.class);
      MainActivity.this.startActivity(intent);
    });
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
      if (requestCode == 123) {
          if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              G.hasWriteAccess = true;
              G.createDirectory();
          } else {
              Toast.makeText(this, "Write to external storage required for loading & saving game", Toast.LENGTH_LONG).show();
          }
      }
  }


  private void openGame(boolean isMultiplayer) {
    Intent intent = new Intent(MainActivity.this, GameActivity.class);
    intent.putExtra("isMultiplayer", isMultiplayer);
    MainActivity.this.startActivity(intent);
  }

  private void resumeGame() {
    Intent intent = new Intent(MainActivity.this, GameActivity.class);
    intent.putExtra("resume", true);
    MainActivity.this.startActivity(intent);
  }
}
