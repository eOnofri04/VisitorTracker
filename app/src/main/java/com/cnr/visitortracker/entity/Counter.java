package com.cnr.visitortracker.entity;

public class Counter {

	private int peopleCounter;

	public Counter( ){
		super( );
		this.peopleCounter = 0;
	}

	public void incrementCounter(int val){
		this.peopleCounter += val;
	}

	public void resetEntity(){ this.peopleCounter = 0; }

	public int getPeopleCounter(){
		return this.peopleCounter;
	}

}
