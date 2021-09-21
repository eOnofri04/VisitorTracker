package com.cnr.visitortracker.activity;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cnr.visitortracker.BuildConfig;
import com.cnr.visitortracker.R;
import com.cnr.visitortracker.constants.Constants;
import com.cnr.visitortracker.service.ExitTrackerService;
import com.cnr.visitortracker.service.RoomTrackerService;

import java.io.File;
import java.util.ArrayList;

public class ExitCounterActivity  extends AppCompatActivity {

	private ArrayList<Button> incrementButtons = new ArrayList<Button>();
	private Button cancel;
	private ArrayList<TextView> displayCounters = new ArrayList<TextView>();;
	private TextView displayClick;

	private ExitTrackerService service;

	private String filename = "exit_counter" + Constants.FILE_EXTENSION;
	private String filepath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exit_counter);

		this.setTitle(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);

		service = new ExitTrackerService(Constants.NO_COLORS);

		filepath = getIntent().getStringExtra(Constants.FILEPATH_KEY);

		//Components reading
		incrementButtons.add((Button) findViewById(R.id.increment_fuchsia));
		incrementButtons.add((Button) findViewById(R.id.increment_green));
		incrementButtons.add((Button) findViewById(R.id.increment_purple));
		incrementButtons.add((Button) findViewById(R.id.increment_yellow));
		incrementButtons.add((Button) findViewById(R.id.increment_blue));
		incrementButtons.add((Button) findViewById(R.id.increment_grey));

		cancel = (Button) findViewById(R.id.cancel_aexit);

		displayCounters.add((TextView) findViewById(R.id.counter_fuchsia));
		displayCounters.add((TextView) findViewById(R.id.counter_green));
		displayCounters.add((TextView) findViewById(R.id.counter_purple));
		displayCounters.add((TextView) findViewById(R.id.counter_yellow));
		displayCounters.add((TextView) findViewById(R.id.counter_blue));
		displayCounters.add((TextView) findViewById(R.id.counter_grey));

		displayClick = (TextView) findViewById(R.id.counter_click_aexit);

		if (service.syncToFile(new File(getExternalFilesDir(filepath), filename))){
			syncCounters();
		} else {
			setEnabled(false);
		}

		// SetUp `i`-th button
		for (int i = 0; i < Constants.NO_COLORS; i++) {
			int finalI = i;
			incrementButtons.get(i).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (service.incrementCounter(finalI)) {
						syncCounters();
					} else {
						Toast.makeText(ExitCounterActivity.this, getString(R.string.unable_write_file), Toast.LENGTH_SHORT).show();
					}
					view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				}
			});
		}

		// SetUp `/0` button
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if ( service.canRollback() ) {
					if ( service.rollback() ) {
						syncCounters();
					} else {
						Toast.makeText(ExitCounterActivity.this, getString(R.string.unable_write_file), Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(ExitCounterActivity.this, getString(R.string.no_del), Toast.LENGTH_SHORT).show();
				}
				view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

	}

	private void syncCounters(){
		for (int i = 0; i < Constants.NO_COLORS; i++){ displayCounters.get(i).setText(Integer.toString(service.getCounter(i))); }
		displayClick.setText(Integer.toString(service.getClickCounter()));
	}

	private void setEnabled(boolean b){
		for (int i = 0; i < Constants.NO_COLORS; i++){ displayCounters.get(i).setEnabled(b); }
		cancel.setEnabled(b);
	}
}
