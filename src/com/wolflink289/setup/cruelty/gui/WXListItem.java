package com.wolflink289.setup.cruelty.gui;

public class WXListItem {
	
	private String text, note;
	
	public WXListItem(String text, String note) {
		this.text = text;
		this.note = note;
	}
	
	public String getText() {
		return text;
	}
	
	public String getNote() {
		return note;
	}
	
	public void set(String text, String note) {
		this.text = text;
		this.note = note;
	}
	
	@Override
	public String toString() {
		return text + ", " + note;
	}
}
