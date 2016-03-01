package com.ruili.target.activitys;

import com.ruili.target.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageButton imgBtnTodayTargets = (ImageButton) findViewById(R.id.imgbtn_today_targets);
		imgBtnTodayTargets.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_today_targets:
			startActivity(new Intent(this, TargetListActivity.class));
			break;

		default:
			break;
		}
	}
}
