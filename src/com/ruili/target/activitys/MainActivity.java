package com.ruili.target.activitys;

import com.ruili.target.R;
import com.ruili.target.entity.User;
import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements OnClickListener {

	private PopupWindow mPopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UmengUpdateAgent.update(this);
		if (-1 == getUserType()) {
			showLoginActivity();
		}
		setContentView(R.layout.activity_main);
		initViews();
	}

	private void showLoginActivity() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}

	private void initViews() {
		ImageButton imgBtnTodayTargets = (ImageButton) findViewById(R.id.imgbtn_today_targets);
		ImageButton imgBtnHistoryTargets = (ImageButton) findViewById(R.id.imgbtn_history_targets);
		ImageButton imgBtnInspectSupervise = (ImageButton) findViewById(R.id.imgbtn_inspect_supervise);
		ImageButton imgBtnSettings = (ImageButton) findViewById(R.id.imgbtn_settings);
		imgBtnTodayTargets.setOnClickListener(this);
		imgBtnHistoryTargets.setOnClickListener(this);
		imgBtnInspectSupervise.setOnClickListener(this);
		imgBtnSettings.setOnClickListener(this);
		switch (getUserType()) {
		case User.TYPE_QC:
			findViewById(R.id.ll_inspect_supervise).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_history_targets).setVisibility(View.GONE);
			findViewById(R.id.ll_today_targets).setVisibility(View.GONE);
			break;
		default:
			findViewById(R.id.ll_inspect_supervise).setVisibility(View.GONE);
			findViewById(R.id.ll_history_targets).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_today_targets).setVisibility(View.VISIBLE);
			break;
		}
		TextView tvOperatorName = (TextView) findViewById(R.id.tv_name);
		tvOperatorName.setText(getString(R.string.operator_name, getUserName()));
	}

	private void clearUserInfo() {
		SharedPreferences mySharedPreferences = getSharedPreferences(User.SHAREDPREFERENCES_KEY, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_today_targets:
			showTargetListActivity(TargetListActivity.TYPE_TODAY);
			break;
		case R.id.imgbtn_history_targets:
			showTargetListActivity(TargetListActivity.TYPE_HISTORY);
			break;
		case R.id.imgbtn_inspect_supervise:
			showTargetListActivity(TargetListActivity.TYPE_INSPECT_SUPERVISE);
			break;
		case R.id.imgbtn_settings:
			showOrHideSettingsMenu();
			break;
		default:
			break;
		}
	}

	private void showTargetListActivity(int type) {
		Intent intent = new Intent(this, TargetListActivity.class);
		intent.putExtra(TargetListActivity.TYPE_KEY, type);
		startActivity(intent);
	}

	private void showOrHideSettingsMenu() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			dismissSettingsMenu();
		} else {
			showSettingsMenu();
		}
	}

	private void dismissSettingsMenu() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}

	private void showSettingsMenu() {
		if (mPopupWindow == null) {
			initSettingsMenu();
		}
		mPopupWindow.showAsDropDown(findViewById(R.id.imgbtn_settings), 10, 10);
	}

	private void initSettingsMenu() {
		View view = LayoutInflater.from(this).inflate(R.layout.widget_quit, null);
		Button btnQuit = (Button) view.findViewById(R.id.btn_quit);
		btnQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismissSettingsMenu();
				clearUserInfo();
				showLoginActivity();
			}
		});
		Button btnChangePassword = (Button) view.findViewById(R.id.btn_change_password);
		btnChangePassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismissSettingsMenu();
				showChangePasswordActivity();
			}
		});
		mPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.update();
	}

	protected void showChangePasswordActivity() {
		startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
	}
}
