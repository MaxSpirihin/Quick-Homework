package com.quickhomework.Classes;

import java.util.ArrayList;

import android.util.Log;

public class Subject {

	public final static int MAX_COUNT_OF_HOME_WORKS = 3;

	// Œ“À¿ƒŒ◊Õ€≈ ÍÓÌÒÚ‡ÌÚ˚
	public static final String LOG_TAG = "myLogs";

	public static final String PATH = "/sdcard/QuickHomework/";

	public String name;
	public int id;
	public boolean isLocked;
	public int countOfHomeWork;
	public ArrayList<String> textOfHomeWork;



	public Subject(String name, int id, boolean isLocked, int countOfHomeWork,
			ArrayList<String> textOfHomeWork) {
		this.id = id;
		this.name = name;
		this.isLocked = isLocked;
		this.countOfHomeWork = countOfHomeWork;
		this.textOfHomeWork = textOfHomeWork;
	}



	public void Print() {
		Log.d(LOG_TAG, "---------------------");
		Log.d(LOG_TAG, "ID = " + String.valueOf(this.id));
		Log.d(LOG_TAG, "name = " + this.name);
		Log.d(LOG_TAG, "isLocked = " + this.isLocked);
		Log.d(LOG_TAG, "countOfHomeWork = " + this.countOfHomeWork);
		Log.d(LOG_TAG, "textOfHomeWork:");
		for (int i = 0; i < textOfHomeWork.size(); i++) {
			Log.d(LOG_TAG, String.valueOf(textOfHomeWork.get(i)));
		}
		Log.d(LOG_TAG, "---------------------");
		Log.d(LOG_TAG, "");
	}



	public static String ComputePath(int id, int number) {
		return PATH
		+ String.valueOf(id)
		+ "_"
		+ String.valueOf(number) + ".jpg";
	}

}
