package com.ruili.target.utils;

import com.ruili.target.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtils {

	private ProgressDialog mProgressDialog;
	final Context mContext;
	private static ProgressDialogUtils mSelf;
	private ProgressDialogUtils(Activity context) {
		mContext = context;
	}
	
	public static synchronized ProgressDialogUtils getInstance(Activity context) {
		if (mSelf == null) {
			mSelf = new ProgressDialogUtils(context);
		}
		return mSelf;
	}

	public void show(int resId) {
		this.show(mContext.getString(resId));
	}

	public void show(CharSequence text) {
		cancel();
		mProgressDialog = ProgressDialog.show(mContext, mContext.getText(R.string.please_wait), text, true);
	}

	public void cancel() {
		if (mProgressDialog != null) {
			mProgressDialog.cancel();
		}
	}
}
