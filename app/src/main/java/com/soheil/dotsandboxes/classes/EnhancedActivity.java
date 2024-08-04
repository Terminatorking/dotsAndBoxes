package com.soheil.dotsandboxes.classes;


import static com.soheil.dotsandboxes.classes.G.currentActivities;

import androidx.appcompat.app.AppCompatActivity;

public class EnhancedActivity extends AppCompatActivity {

  @Override
  protected void onPause() {
    super.onPause();
    currentActivities.remove(this);
    refreshMediaPlayer();
  }

  @Override
  protected void onResume() {
    super.onResume();
    currentActivities.add(this);
    refreshMediaPlayer();
  }

  private void refreshMediaPlayer() {
    if (!Settings.isEnableMusic()) {
      return;
    }

    G.handler.postDelayed(() -> {
      if (currentActivities.isEmpty()) {
        G.musicPlayer.pause();
      } else {
        if (!G.musicPlayer.isPlaying()) {
          G.musicPlayer.start();
        }
      }
    }, 100);
  }
}
