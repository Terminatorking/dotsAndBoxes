package com.soheil.dotsandboxes.activities;

import android.app.Dialog;
import android.os.Bundle;

import android.widget.ImageButton;

import com.soheil.dotsandboxes.R;
import com.soheil.dotsandboxes.classes.EnhancedActivity;
import com.soheil.dotsandboxes.dialog.OptionDialog;
import com.soheil.dotsandboxes.views.GameView;

import java.util.Objects;


public class GameActivity extends EnhancedActivity {

  private GameView gameView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);

    final boolean isMultiplayer = Objects.requireNonNull(getIntent().getExtras()).getBoolean("isMultiplayer");
    final boolean mustResume = getIntent().getExtras().getBoolean("resume");

    ImageButton btn_reset = findViewById(R.id.btn_reset);
    ImageButton btn_option = findViewById(R.id.btn_option);
    gameView = findViewById(R.id.gameview);

    btn_reset.setOnClickListener(view -> gameView.resetGame());

    btn_option.setOnClickListener(view -> {
      gameView.startGame(isMultiplayer);
      Dialog dialog = new OptionDialog(GameActivity.this);
      dialog.show();
    });

    if (mustResume) {
      gameView.loadGame();
    } else {
      gameView.startGame(isMultiplayer);
    }
  }


  public GameView getGameView() {
    return gameView;
  }

  @Override
  protected void onPause() {
    super.onPause();
    gameView.saveGame();
  }
}
