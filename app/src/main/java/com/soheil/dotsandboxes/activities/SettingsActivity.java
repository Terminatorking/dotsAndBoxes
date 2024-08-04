package com.soheil.dotsandboxes.activities;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.soheil.dotsandboxes.R;
import com.soheil.dotsandboxes.classes.Settings;

public class SettingsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    Switch chk_enableHighPerformance = (Switch) findViewById(R.id.chk_enableHighPerformance);
    Switch chk_enableMusic = (Switch) findViewById(R.id.chk_enableMusic);
    Switch chk_enableSfx = (Switch) findViewById(R.id.chk_enableSfx);

    chk_enableHighPerformance.setChecked(Settings.isEnableHighPerformance());
    chk_enableMusic.setChecked(Settings.isEnableMusic());
    chk_enableSfx.setChecked(Settings.isEnableSfx());

    chk_enableHighPerformance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Settings.getSharedPreferenceEditor()
          .putBoolean("enable_high_performance", isChecked)
          .apply();
      }
    });

    chk_enableMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Settings.getSharedPreferenceEditor()
          .putBoolean("enable_music", isChecked)
          .apply();
      }
    });

    chk_enableSfx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Settings.getSharedPreferenceEditor()
          .putBoolean("enable_sfx", isChecked)
          .apply();
      }
    });
  }
}
