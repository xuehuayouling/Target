package com.ruili.target.utils;

import android.util.Log;

public class Logger {

	private static final boolean DEBUG = true;
	public static void debug(String tag, String msg) {
		if (DEBUG) {
			Log.d(tag, msg);
		}
	}
}
