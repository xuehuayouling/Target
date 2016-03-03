package com.ruili.target.activitys;

import com.ruili.target.entity.User;
import com.ruili.target.utils.ImmediatelyShowToast;
import com.ruili.target.utils.ProgressDialogUtils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class BaseActivity extends Activity {

	protected ImmediatelyShowToast mToast;
	protected ProgressDialogUtils mProgressDialogUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initToast();
		initProgressDialog();
	}
	
	private void initToast() {
		mToast = ImmediatelyShowToast.getInstance(getApplication());
	}
	
	private void initProgressDialog() {
		mProgressDialogUtils = ProgressDialogUtils.getInstance(this);
	}

	public ImmediatelyShowToast getToast() {
		return mToast;
	}

	public ProgressDialogUtils getProgressDialogUtils() {
		return mProgressDialogUtils;
	}
	
	public int getUserOperatorID() {
		SharedPreferences sharedPreferences = getSharedPreferences(User.SHAREDPREFERENCES_KEY, Activity.MODE_PRIVATE);
		return sharedPreferences.getInt(User.SHAREDPREFERENCES_OPERATOR_ID, -1);
	}
	
	public int getUserType() {
		SharedPreferences sharedPreferences = getSharedPreferences(User.SHAREDPREFERENCES_KEY, Activity.MODE_PRIVATE);
		return sharedPreferences.getInt(User.SHAREDPREFERENCES_TYEP, -1);
	}

}
