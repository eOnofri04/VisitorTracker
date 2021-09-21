package com.cnr.visitortracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.cnr.visitortracker.BuildConfig;
import com.cnr.visitortracker.R;
import com.cnr.visitortracker.constants.Constants;

import java.sql.Timestamp;

public class MainMenuActivity extends AppCompatActivity {

	String filepath = "";
	String filepath_readable = "";

	SwitchCompat customSaveDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		Button roomCounter;
		Button exitCounter;
		SwitchCompat darkTheme;
		SwitchCompat leftHanded;
		SwitchCompat english;

		this.setTitle(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);

		//Components reading
		roomCounter = (Button) findViewById(R.id.launch_room_counter);
		exitCounter = (Button) findViewById(R.id.launch_exit_counter);

		darkTheme = (SwitchCompat) findViewById(R.id.switch_dark_theme);
		leftHanded = (SwitchCompat) findViewById(R.id.switch_left_handed);
		english = (SwitchCompat) findViewById(R.id.switch_english);
		customSaveDir = (SwitchCompat) findViewById(R.id.switch_custom_save_directory);

		darkTheme.setEnabled(false);
		leftHanded.setEnabled(false);
		english.setEnabled(false);

		setFilepath();

		// Launch room counter
		roomCounter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent theIntent = new Intent(MainMenuActivity.this, RoomCounterActivity.class);
				theIntent.putExtra(Constants.FILEPATH_KEY, filepath);
				startActivity(theIntent);
				view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		// Launch exit counter
		exitCounter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent theIntent = new Intent(MainMenuActivity.this, ExitCounterActivity.class);
				theIntent.putExtra(Constants.FILEPATH_KEY, filepath);
				startActivity(theIntent);
				view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		customSaveDir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setFilepath();
				Toast.makeText(MainMenuActivity.this, getString(R.string.set_save_dir) + "`" + filepath_readable + "`", Toast.LENGTH_SHORT).show();
				view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}

		});

	}

	void setFilepath(){
		String subdir = Constants.sdfDIR.format(new Timestamp(System.currentTimeMillis()));

		this.filepath = "" + (customSaveDir.isChecked() ? Constants.CUSTOM_SAVE_DIR : Constants.DEFAULT_SAVE_DIR) + subdir;
		this.filepath_readable = "" + (customSaveDir.isChecked() ? Constants.CUSTOM_SAVE_DIR_READABLE : Constants.DEFAULT_SAVE_DIR_READABLE) + subdir;

		return ;
	}
}
