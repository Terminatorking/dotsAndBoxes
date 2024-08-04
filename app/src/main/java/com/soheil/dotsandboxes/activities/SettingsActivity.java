package com.soheil.dotsandboxes.activities;

import android.os.Bundle;
import android.widget.SeekBar;
import androidx.appcompat.widget.SwitchCompat;
import com.soheil.dotsandboxes.R;
import com.soheil.dotsandboxes.classes.EnhancedActivity;
import com.soheil.dotsandboxes.classes.G;
import com.soheil.dotsandboxes.classes.Settings;


public class SettingsActivity extends EnhancedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchCompat chk_enableHighPerformance = findViewById(R.id.chk_enableHighPerformance);
        SwitchCompat chk_enableMusic = findViewById(R.id.chk_enableMusic);
        SwitchCompat chk_enableSfx = findViewById(R.id.chk_enableSfx);
        SeekBar slider_musicVolume = findViewById(R.id.slider_musicVolume);

        chk_enableHighPerformance.setChecked(Settings.isEnableHighPerformance());
        chk_enableMusic.setChecked(Settings.isEnableMusic());
        chk_enableSfx.setChecked(Settings.isEnableSfx());
        slider_musicVolume.setProgress((int) (Settings.getMusicVolume() * 100));

        chk_enableHighPerformance.setOnCheckedChangeListener((compoundButton, isChecked) -> Settings.getSharedPreferenceEditor()
                .putBoolean("enable_high_performance", isChecked)
                .apply());

        chk_enableMusic.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            Settings.getSharedPreferenceEditor()
                    .putBoolean("enable_music", isChecked)
                    .apply();

            if (!isChecked) {
                G.musicPlayer.pause();
            } else {
                G.musicPlayer.start();
            }
        });

        chk_enableSfx.setOnCheckedChangeListener((compoundButton, isChecked) -> Settings.getSharedPreferenceEditor()
                .putBoolean("enable_sfx", isChecked)
                .apply());

        slider_musicVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                Settings.getSharedPreferenceEditor()
                        .putInt("music_volume", value)
                        .apply();

                float volume = Settings.getMusicVolume();
                G.musicPlayer.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
