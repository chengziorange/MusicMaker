package com.entity;

public class AudioChip {
	private String name;
	private int time;
	
	public AudioChip(String name,int time) {
		this.name = name;
		this.time = time;
	}
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
