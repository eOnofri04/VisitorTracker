package com.cnr.visitortracker.service;

import com.cnr.visitortracker.constants.Constants;
import com.cnr.visitortracker.entity.Counter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RoomTrackerService {


	private File externalFile;
	private Counter ctr;
	private int nl;
	private int clickCounter;


	public RoomTrackerService( ){
		this.externalFile = null;
		this.clickCounter = 0;
		this.ctr = new Counter();
		this.nl = 0;
	}

	public boolean syncToFile(File externalFile){

		this.externalFile = externalFile;

		if (this.checkFile( ) && this.readCountersFromFile()){
			return true;
		}

		this.resetService( );

		return false;
	}

	public boolean incrementCounter(int val){
		if (saveState(val)){
			this.ctr.incrementCounter(val);
			this.click();
			return true;
		}
		return false;
	}

	public boolean canRollback(){
		return this.nl > 0;
	}

	public boolean rollback(){
		int operand = 0;

		RandomAccessFile f = null;

		try {
			f = new RandomAccessFile(this.externalFile, "rw");
			long length;

			if (this.nl > 1){
				length = f.length() - 1; // start checking from the last char
				byte b;
				do {
					length--;
					f.seek(length);
					b = f.readByte();
				} while(b != 10);// && length > 0);

				length++;// = length == 0 ? length : length+1;

			} else { length = 0; }

			f.seek(length);
			operand = Integer.parseInt(f.readLine().split(" ")[0]);

			f.setLength(length);
			this.nl--;
			f.close();

		} catch (IOException e) {
			e.printStackTrace();

			return false;
		}

		this.ctr.incrementCounter(-operand);
		this.click();

		return true;
	}

	private boolean saveState(int val){
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		String fileContent = val + Constants.DATA_SEPARATOR + Constants.sdf.format(timestamp) + "\n";

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(externalFile, true);
			fos.write(fileContent.getBytes());
			this.nl++;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private boolean checkFile(){

		if (this.externalFile == null){ return false; }

		if (!this.externalFile.exists()){
			try {
				this.externalFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		return this.externalFile.isFile() && this.externalFile.canRead() && this.externalFile.canWrite();
	}

	private boolean readCountersFromFile(){
		BufferedReader br = null;
		String line = null;

		this.ctr.resetEntity();
		this.nl = 0;
		this.clickCounter = 0;

		try {
			br = new BufferedReader(new FileReader(this.externalFile));

			line = br.readLine();
			while (line != null && line.length()>0) {
				this.ctr.incrementCounter(Integer.parseInt(line.split(Constants.DATA_SEPARATOR)[0]));
				line = br.readLine();
				this.nl++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private void resetService(){
		this.externalFile = null;
		this.ctr.resetEntity();
		this.nl = 0;
	}

	public int getPeopleCounter() {
		return this.ctr.getPeopleCounter();
	}

	public int getClickCounter() {
		return this.clickCounter;
	}

	private void click(){
		this.clickCounter++;
	}
}
