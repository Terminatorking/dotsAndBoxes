package com.soheil.dotsandboxes.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.soheil.dotsandboxes.R;
import com.soheil.dotsandboxes.classes.G;
import com.soheil.dotsandboxes.classes.OptionDialog;
import com.soheil.dotsandboxes.views.GameView;


public class GameActivity extends AppCompatActivity {

  private GameView gameView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);

    final boolean isMultiplayer = getIntent().getExtras().getBoolean("isMultiplayer");
    final boolean mustResume = getIntent().getExtras().getBoolean("resume");

    ImageButton btn_reset = (ImageButton) findViewById(R.id.btn_reset);
    ImageButton btn_option = (ImageButton) findViewById(R.id.btn_option);
    gameView = (GameView) findViewById(R.id.gameview);

    requestWritePermission();

    btn_reset.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        gameView.resetGame(isMultiplayer);
      }
    });

    btn_option.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        gameView.resetGame(isMultiplayer);
        Dialog dialog = new OptionDialog(GameActivity.this);
        dialog.show();
      }
    });

    if (mustResume) {
      gameView.loadGame();
    } else {
      gameView.resetGame(isMultiplayer);
    }
  }


  public GameView getGameView() {
    return gameView;
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
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
}
