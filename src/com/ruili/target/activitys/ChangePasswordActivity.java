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
import com.ruili.target.entity.ResponseDTO;
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

public class ChangePasswordActivity extends BaseActivity implements OnClickListener {

	private EditText mEtOldPassword;
	private EditText mEtNewPassword;
	private EditText mEtNewPasswordRepeat;
	private RequestQueue mQueue;
	private static final String TAG = ChangePasswordActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		initViews();
		initRequestQueue();
	}

	private void initViews() {
		mEtOldPassword = (EditText) findViewById(R.id.et_old_password);
		mEtNewPassword = (EditText) findViewById(R.id.et_new_password);
		mEtNewPasswordRepeat = (EditText) findViewById(R.id.et_new_password_repeat);
		Button btnChangePassword = (Button) findViewById(R.id.btn_change_password);
		btnChangePassword.setOnClickListener(this);
	}

	private void initRequestQueue() {
		mQueue = Volley.newRequestQueue(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_change_password) {
			if (TextUtils.isEmpty(mEtOldPassword.getText().toString())
					|| TextUtils.isEmpty(mEtNewPassword.getText().toString())
					|| TextUtils.isEmpty(mEtNewPasswordRepeat.getText().toString())) {
				mToast.show(R.string.input_info_not_complete);
			} else if (mEtNewPassword.getText().toString().equals(mEtNewPasswordRepeat.getText().toString())) {
				changePassword();
			} else {
				mToast.show(R.string.new_passwords_not_same);
			}
		}
	}

	private void showLoginActivity() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}

	private void changePassword() {
		final String oldPassword = mEtOldPassword.getText().toString();
		final String newPassword = mEtNewPassword.getText().toString();
		if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword)) {
			mToast.show(R.string.valid_user_or_password);
		} else {
			mProgressDialogUtils.show(R.string.logining);
			StringRequest stringRequest = new StringRequest(Method.PUT, getChangePasswordUrl(),
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							mProgressDialogUtils.cancel();
							decodeUserResponse(response);
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							mProgressDialogUtils.cancel();
							mToast.show(R.string.netword_fail);
							Logger.debug(TAG, "login fail -->  " + error.toString());
						}
					}) {

				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> map = new HashMap<String, String>();
					map.put("old_password", oldPassword);
					map.put("new_password", newPassword);
					return map;
				}

			};
			mQueue.add(stringRequest);
		}
	}

	private String getChangePasswordUrl() {
		return Constant.BASE_URL + String.format("/api/v1/index/%d/changepwd", getUserOperatorID());
	}

	private void decodeUserResponse(String response) {
		Logger.debug(TAG, "decodeUserResponse -- >" + response);
		try {
			UserDTO dto = JsonUtil.parseObject(response, UserDTO.class);
			if (dto.isValid()) {
				mToast.show(R.string.password_change_success);
				clearUserInfo();
				showLoginActivity();
			} else {
				mToast.show(R.string.password_change_fail);
				Logger.debug(TAG, "decodeUserResponse" + " --> " + getString(R.string.no_valid_data) + response);
			}
		} catch (Exception e) {
			mToast.show(getText(R.string.service_fail) + response);
			e.printStackTrace();
			Logger.debug(TAG, "decodeUserResponse" + " --> " + getString(R.string.can_not_decode_data) + response);
		}
	}

	private void clearUserInfo() {
		SharedPreferences mySharedPreferences = getSharedPreferences(User.SHAREDPREFERENCES_KEY, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.clear();
		editor.commit();
	}
}
