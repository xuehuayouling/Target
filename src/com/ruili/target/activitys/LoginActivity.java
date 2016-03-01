package com.ruili.target.activitys;

import com.ruili.target.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Button btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_login) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

}
