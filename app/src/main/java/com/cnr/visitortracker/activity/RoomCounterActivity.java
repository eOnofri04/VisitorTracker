package com.cnr.visitortracker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cnr.visitortracker.BuildConfig;
import com.cnr.visitortracker.R;
import com.cnr.visitortracker.constants.Constants;
import com.cnr.visitortracker.service.VisitorTrackerService;

import java.io.File;
import java.sql.Timestamp;

public class RoomCounterActivity extends AppCompatActivity {

	private Button addOne;
	private Button subOne;
	private Button cancel;

	private TextView display_people;
	private TextView display_click;

	private VisitorTrackerService service;

	String filename = "";
	String filepath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_counter);

		this.setTitle(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);

		String[] rooms = getResources().getStringArray(R.array.rooms);

		ImageButton config;

		AutoCompleteTextView roomSelector;
		ArrayAdapter<String> roomAdapter;

		service = new VisitorTrackerService();

		filepath = getIntent().getStringExtra(Constants.FILEPATH_KEY);

		//Components reading
		addOne = (Button)findViewById(R.id.addOne);
		subOne = (Button)findViewById(R.id.subOne);
		cancel = (Button)findViewById(R.id.cancel);
		config = (ImageButton)findViewById(R.id.configButton);
		display_people = (TextView)findViewById(R.id.counter_persone);
		display_click = (TextView)findViewById(R.id.counter_click);
		roomSelector = (AutoCompleteTextView) findViewById(R.id.room_selector);

		this.setEnabled(false);

		// Stop from writing if you do not have permissions
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(RoomCounterActivity.this, getString(R.string.RW_permissions), Toast.LENGTH_SHORT).show();
		}

		// SetUp `+1` button
		addOne.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (service.incrementCounter( +1 )) {
					syncCounters();
				} else {
					Toast.makeText(RoomCounterActivity.this, getString(R.string.unable_write_file), Toast.LENGTH_SHORT).show();
				}
				view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		// SetUp `-1` button
		subOne.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (service.incrementCounter( -1 )) {
					syncCounters();
				} else {
					Toast.makeText(RoomCounterActivity.this, getString(R.string.unable_write_file), Toast.LENGTH_SHORT).show();
				}
				view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		// SetUp `/0` button
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if ( service.canRollback() ) {
					if ( service.rollback() ) {
						syncCounters();
					} else {
						Toast.makeText(RoomCounterActivity.this, getString(R.string.unable_write_file), Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(RoomCounterActivity.this, getString(R.string.no_del), Toast.LENGTH_SHORT).show();
				}
				view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});

		// SetUp `I` button

		config.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(RoomCounterActivity.this, SettingsPopup.class));
				view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});


		// SetUp room dropdown menu
		roomAdapter = new ArrayAdapter<String>(this, R.layout.room_selector_item, rooms);
		roomSelector.setAdapter(roomAdapter);
		roomSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

				view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				filename = adapterView.getItemAtPosition(i).toString() + Constants.FILE_EXTENSION;

				if ( service.syncToFile( new File(getExternalFilesDir(filepath), filename) ) ){
					setEnabled(true);
					syncCounters();
					Toast.makeText(RoomCounterActivity.this, "Selected: `" + filename + "`", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(RoomCounterActivity.this, getString(R.string.unable_select_file), Toast.LENGTH_SHORT).show();
				}
			}

		});
	}

	private void syncCounters(){
		display_people.setText(Integer.toString(service.getPeopleCounter()));
		display_click.setText(Integer.toString(service.getClickCounter()));
	}

	private void setEnabled(boolean b){
		addOne.setEnabled(b);
		subOne.setEnabled(b);
		cancel.setEnabled(b);
	}

}