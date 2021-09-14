package com.cnr.visitortracker.entity;

public class Counters {

	private int peopleCounter;
	private int clickCounter;

	public Counters( ){
		super( );
		this.clickCounter = 0;
		this.peopleCounter = 0;
	}

	public void click(){
		this.clickCounter++;
	}

	public void incrementCounter(int val){
		this.peopleCounter += val;
	}

	public void resetEntity(){
		this.peopleCounter = 0;
		this.clickCounter = 0;
	}

	public int getPeopleCounter(){
		return this.peopleCounter;
	}

	public int getClickCounter(){
		return this.clickCounter;
	}
}
