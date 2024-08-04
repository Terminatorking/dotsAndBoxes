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

    G.handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if (currentActivities.size() == 0) {
          G.musicPlayer.pause();
        } else {
          if (!G.musicPlayer.isPlaying()) {
            G.musicPlayer.start();
          }
        }
      }
    }, 100);
  }
}
