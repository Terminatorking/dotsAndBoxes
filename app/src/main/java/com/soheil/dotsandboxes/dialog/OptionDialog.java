package com.soheil.dotsandboxes.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.soheil.dotsandboxes.R;
import com.soheil.dotsandboxes.activities.GameActivity;
import com.soheil.dotsandboxes.classes.Settings;

import java.util.Objects;


public class OptionDialog extends Dialog {

  private final GameActivity activity;

  public OptionDialog(GameActivity activity) {
    super(activity);
    this.activity = activity;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.dialog_option);

    Objects.requireNonNull(getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    Button btn_ok = findViewById(R.id.btn_ok);
    final EditText edt_cols = findViewById(R.id.edt_cols);
    final EditText edt_rows =  findViewById(R.id.edt_rows);

    edt_cols.setText(String.valueOf(Settings.getCols()));
    edt_rows.setText(String.valueOf(Settings.getRows()));

    btn_ok.setOnClickListener(view -> {
      int cols = Integer.parseInt(edt_cols.getText().toString());
      int rows = Integer.parseInt(edt_rows.getText().toString());

      Settings.setRows(rows);
      Settings.setCols(cols);

      activity.getGameView().resetGame();
      dismiss();
    });
  }
}