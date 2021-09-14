package com.cnr.visitortracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

	private static final String FILE_EXTENSION = ".txt";
	private static final String SAVE_DIR = "../../com.cnr.visitortracker/";
	//private static final String SAVE_DIR = "";

	private static final String[] rooms = {"00_Ingresso","01_Paolina","02_Enea","03_Apollo","04_Ratto","05_Portico","06_Paolina","07_Satiro","08_Caravaggio","09_Pinacoteca","10_Uscita","90_extra","91_extra","92_extra","93_extra","94_extra"};

	private Button addOne;
	private Button subOne;
	private Button cancel;
	private ImageButton info;
	private TextView display_people;
	private TextView display_click;
	private AutoCompleteTextView roomSelector;
	private int counter_people = 0;
	private int counter_click = 0;
//  private Deque<Integer> last_op = new LinkedList<Integer>();
	private int counter_lines = 0;

	ArrayAdapter<String> roomAdapter;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH mm ss");
	private static final SimpleDateFormat sdfDIR = new SimpleDateFormat("yyyy_MM_dd");

	String filename = "";
	String filepath = "";

	static final int max_memory = 25;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		filepath = "" + SAVE_DIR + sdfDIR.format(new Timestamp(System.currentTimeMillis()));

		//Components reading
		addOne = (Button)findViewById(R.id.addOne);
		subOne = (Button)findViewById(R.id.subOne);
		cancel = (Button)findViewById(R.id.cancel);
		info = (ImageButton)findViewById(R.id.infoButton);
		display_people = (TextView)findViewById(R.id.counter_persone);
		display_click = (TextView)findViewById(R.id.counter_click);
		roomSelector = (AutoCompleteTextView) findViewById(R.id.room_selector);

		enableUsage(false);

		// Stop from writing if you do not have permissions
		if (!isExternalStorageAvailableRW()){
			Toast.makeText(MainActivity.this, "I DO NOT HAVE RW PERMISSIONS!", Toast.LENGTH_SHORT).show();
		}

		// SetUp `+1` button
		addOne.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				saveState(+1);
				counter_people++;
				counter_click++;
				counter_lines++;
				syncCounters();
				view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//				last_op.addLast(new Integer(+1));
//				if (last_op.size() > max_memory){
//					last_op.removeFirst();
//				}
			}
		});

		// SetUp `-1` button
		subOne.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				saveState(-1);
				counter_people--;
				counter_click++;
				counter_lines++;
				syncCounters();
				view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//				last_op.addLast(new Integer(-1));
//					if (last_op.size() > max_memory){
//					last_op.removeFirst();
//				}
			}
		});

		// SetUp `/0` button
//		cancel.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				if (last_op.size() > 0) {
//					saveState(0);
//					counter_people -= last_op.pollLast().intValue();
//					counter_click++;
//					display_people.setText(Integer.toString(counter_people));
//					display_click.setText(Integer.toString(counter_click));
//				} else {
//					Toast.makeText(MainActivity.this, "Nessuna operazione annullabile in memoria!", Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (counter_lines > 0) {
					counter_people -= deleteLast( );
					counter_click++;
					counter_lines--;
					syncCounters();
					view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				} else {
					Toast.makeText(MainActivity.this, "Nessuna operazione da annullare!", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// SetUp `I` button

		info.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this, Pop.class));
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
				filename = adapterView.getItemAtPosition(i).toString() + FILE_EXTENSION;
				enableUsage(true);
				counter_click = 0;
				readCountersFromFile();
				syncCounters();
				Toast.makeText(MainActivity.this, "Selected: `"+ filename + "`", Toast.LENGTH_SHORT).show();
			}
		});
		roomSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				filename = adapterView.getItemAtPosition(i).toString() + FILE_EXTENSION;
				enableUsage(true);
				Toast.makeText(MainActivity.this, "Selected: `"+ filename + "`", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				enableUsage(false);
			}
		});
	}

	private void readCountersFromFile() {

		File externalFile = new File(getExternalFilesDir(filepath), filename);
		BufferedReader br = null;
		String line = null;

		counter_people = 0;
		counter_lines = 0;

		if (externalFile.isFile()) {
			try {
				br = new BufferedReader(new FileReader(externalFile));

				line = br.readLine();
				while (line != null) {
					counter_people += Integer.parseInt(line.split(" ")[0]);
					line = br.readLine();
					counter_lines++;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isExternalStorageAvailableRW(){
		String extStorageState = Environment.getExternalStorageState();
		return extStorageState.equals(Environment.MEDIA_MOUNTED);
	}

	private boolean saveState(int value){

		boolean done = false;
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		String fileContent = value + " " + sdf.format(timestamp) + "\n";

		File externalFile = new File(getExternalFilesDir(filepath), filename);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(externalFile, true);
			fos.write(fileContent.getBytes());
			done = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!done){
			Toast.makeText(MainActivity.this, "Unable to save.", Toast.LENGTH_SHORT).show();
		}

		return done;
	}

	private int deleteLast(){
		int operand = 0;
		RandomAccessFile f = null;
		try {
			File externalFile = new File(getExternalFilesDir(filepath), filename);
			f = new RandomAccessFile(externalFile, "rw");
			long length = f.length() - 1; // start checking from the last char

			byte b;
			do {
				length--;
				f.seek(length);
				b = f.readByte();
			} while(b != 10 && length > 0);

			length = length == 0 ? length : length+1;

			f.seek(length);
			operand = Integer.parseInt(f.readLine().split(" ")[0]);

			f.setLength(length);
			f.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return operand;
	}

	private void syncCounters(){
		display_people.setText(Integer.toString(counter_people));
		display_click.setText(Integer.toString(counter_click));
	}

	void enableUsage(boolean b){
		addOne.setEnabled(b);
		subOne.setEnabled(b);
		cancel.setEnabled(b);
	}
}