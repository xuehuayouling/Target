package com.ruili.target.activitys;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ruili.target.R;
import com.ruili.target.entity.User;
import com.ruili.target.entity.UserDTO;
import com.ruili.target.utils.Constant;
import com.ruili.target.utils.JsonUtil;
import com.ruili.target.utils.Logger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private EditText mETUserName;
	private EditText mETPassword;
	private RequestQueue mQueue;
	private static final String TAG = LoginActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initViews();
		initRequestQueue();
	}

	private void initViews() {
		mETUserName = (EditText) findViewById(R.id.et_login_user_name);
		mETPassword = (EditText) findViewById(R.id.et_login_password);
		Button btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(this);
	}


	private void initRequestQueue() {
		mQueue = Volley.newRequestQueue(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_login) {
			login();
		}
	}

	private void showMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void login() {
		final String username = mETUserName.getText().toString();
		final String password = mETPassword.getText().toString();
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			mToast.show(R.string.valid_user_or_password);
		} else {
			mProgressDialogUtils.show(R.string.logining);
			StringRequest stringRequest = new StringRequest(Method.POST, getLoginUrl(),
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							Logger.debug(TAG, "login success -->  " + response);
							mProgressDialogUtils.cancel();
							decodeResponse(response);
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Logger.debug(TAG, "login fail -->  " + error.toString());
							mProgressDialogUtils.cancel();
							mToast.show(R.string.netword_fail);
						}
					}) {

				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> map = new HashMap<String, String>();
					map.put("username", username);
					map.put("password", password);
					return map;
				}

			};
			mQueue.add(stringRequest);
		}
	}

	private String getLoginUrl() {
		return Constant.USER_LOGIN;
	}

	private void decodeResponse(String response) {
		Logger.debug(TAG, response);
		try {
			UserDTO dto = JsonUtil.parseObject(response, UserDTO.class);
			if (dto.isValid()) {
				User user = dto.getData();
				saveUserInfo(user);
				showMainActivity();
			} else {
				mToast.show(R.string.valid_user_or_password);
			}
		} catch (Exception e) {
			mToast.show(getText(R.string.service_fail) + response);
			e.printStackTrace();
		}
	}

	private void saveUserInfo(User user) {
		SharedPreferences mySharedPreferences = getSharedPreferences(User.SHAREDPREFERENCES_KEY, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putInt(User.SHAREDPREFERENCES_OPERATOR_ID, user.getOperator_id());
		editor.putInt(User.SHAREDPREFERENCES_TYEP, user.getType());
		editor.putString(User.SHAREDPREFERENCES_NAME, user.getName());
		editor.commit();
	}

}
