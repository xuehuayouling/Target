package com.ruili.target.utils;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class ImmediatelyShowToast {

	private Toast mToast;
	final Context mContext;
	private static ImmediatelyShowToast mSelf;
	private ImmediatelyShowToast(Application context) {
		mContext = context;
	}
	
	public static synchronized ImmediatelyShowToast getInstance(Application context) {
		if (mSelf == null) {
			mSelf = new ImmediatelyShowToast(context);
		}
		return mSelf;
	}

	public void show(int resId) {
		this.show(resId, Toast.LENGTH_SHORT);
	}

	public void show(CharSequence text) {
		this.show(text, Toast.LENGTH_SHORT);
	}

	public void show(int resId, int duration) {
		this.show(mContext.getString(resId), duration);
	}

	public void show(CharSequence text, int duration) {
		cancel();
		mToast = Toast.makeText(mContext, text, duration);
		mToast.show();
	}

	public void cancel() {
		if (mToast != null) {
			mToast.cancel();
		}
	}
}
