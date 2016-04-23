package com.ruili.target.activitys;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.client.android.CaptureActivity;
import com.ruili.target.R;
import com.ruili.target.entity.Category;
import com.ruili.target.entity.CheckTime;
import com.ruili.target.entity.Employee;
import com.ruili.target.entity.EmployeeListDTO;
import com.ruili.target.entity.ResponseDTO;
import com.ruili.target.entity.SimpleResultDTO;
import com.ruili.target.fragments.DetailFragment;
import com.ruili.target.fragments.MainFragment;
import com.ruili.target.utils.Constant;
import com.ruili.target.utils.DateUtils;
import com.ruili.target.utils.DecodeJsonResponseUtils;
import com.ruili.target.utils.JsonUtil;
import com.ruili.target.utils.Logger;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class TargetListActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = TargetListActivity.class.getSimpleName();
	private static final int SCAN_REQUEST_CODE = 1;
	private RequestQueue mQueue;
	private MainFragment mMainFragment;
	private DetailFragment mDetailFragment;
	private List<CheckTime> mCheckTimes;
	// 今日指标、历史指标、监督管理
	public static final String TYPE_KEY = "type";
	public static final int TYPE_TODAY = 0;
	public static final int TYPE_HISTORY = 1;
	public static final int TYPE_INSPECT_SUPERVISE = 2;
	private int mType;
	private TextView mTVDate;
	private TextView mTVTime;
	private TextView mTVEmployee;
	private CheckBox mCbScan;
	public static final int OPETATOR_ID_INVALID = -1;
	private int mOperatorID = OPETATOR_ID_INVALID;
	private PopupWindow mTimeMenus;
	private ArrayAdapter<CheckTime> mTimeMenuAdapter;
	private PopupWindow mEmployeeMenus;
	private ArrayAdapter<Employee> mEmployeeMenuAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mType = getIntent().getIntExtra(TYPE_KEY, -1);
		initRequestQueue();
		setContentView(R.layout.activity_target_list);
		initActionbar();
		FragmentManager manager = getFragmentManager();
		mMainFragment = (MainFragment) manager.findFragmentById(R.id.fragment_main);
		mMainFragment.setActivity(this);
		mDetailFragment = (DetailFragment) manager.findFragmentById(R.id.fragment_detail);
		mDetailFragment.setActivity(this);
		initOperatorID();
	}

	private void initActionbar() {
		initImageViewBack();
		initTextViewDate();
		initTextViewEmployee();
		initTextViewTime();
		initTextViewScan();
		initCheckBoxUnchecked();
		initImageViewMenu();
	}

	private void initTextViewScan() {
		TextView tvScan = (TextView) findViewById(R.id.tv_scan);
		tvScan.setOnClickListener(this);
		if (mType == TYPE_TODAY) {
			tvScan.setVisibility(View.VISIBLE);
		} else {
			tvScan.setVisibility(View.GONE);
		}
	}
	
	private void initCheckBoxUnchecked() {
		mCbScan = (CheckBox) findViewById(R.id.ctv_unchecked);
		mCbScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mDetailFragment.setChecked(mCbScan.isChecked() ? "2" : null);
			}
		});
		if (mType == TYPE_INSPECT_SUPERVISE) {
			mCbScan.setVisibility(View.VISIBLE);
		} else {
			mCbScan.setVisibility(View.GONE);
		}
	}

	private void initTextViewEmployee() {
		mTVEmployee = (TextView) findViewById(R.id.tv_employee);
		mTVEmployee.setOnClickListener(this);
		if (mType == TYPE_INSPECT_SUPERVISE) {
			mTVEmployee.setVisibility(View.VISIBLE);
		} else {
			mTVEmployee.setVisibility(View.GONE);
		}
	}

	private void initImageViewBack() {
		ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
		ivBack.setOnClickListener(this);
	}

	private void initImageViewMenu() {
		ImageView ivMenu = (ImageView) findViewById(R.id.iv_menu);
		ivMenu.setOnClickListener(this);
	}

	private void initTextViewTime() {
		mTVTime = (TextView) findViewById(R.id.tv_time);
		if (mType == TYPE_TODAY) {
			mTVTime.setVisibility(View.VISIBLE);
		} else {
			mTVTime.setVisibility(View.GONE);
		}
	}
	
	private void initTextViewDate() {
		mTVDate = (TextView) findViewById(R.id.tv_date);
		Calendar c = Calendar.getInstance();
		if (mType == TYPE_HISTORY) {
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
		}
		Date date = new Date(c.getTimeInMillis());
		mTVDate.setText(getDateString(date));
		mTVDate.setOnClickListener(this);
		if (mType == TYPE_HISTORY || mType == TYPE_INSPECT_SUPERVISE) {
			mTVDate.setVisibility(View.VISIBLE);
		} else {
			mTVDate.setVisibility(View.GONE);
		}
	}

	private void initOperatorID() {
		if (mType == TYPE_HISTORY || mType == TYPE_TODAY) {
			mOperatorID = getUserOperatorID();
		} else {
			mOperatorID = OPETATOR_ID_INVALID;
		}
	}

	private void initRequestQueue() {
		mQueue = Volley.newRequestQueue(this);
	}

	public RequestQueue getRequestQueue() {
		if (mQueue == null) {
			initRequestQueue();
		}
		return mQueue;
	}

	public int getType() {
		return mType;
	}

	public void onMainListItemClick(Category category) {
		mTVTime.setText(R.string.time_now);
		setCheckTimes(category.getChecktime());
		updateDetailsList(category.getId(), CheckTime.CHECK_TIME_NULL, mTVDate.getText().toString());
	}

	private void updateDetailsList(int categoryId, int checktimeID, String date) {
		mDetailFragment.updateSubcategoryList(categoryId, checktimeID, date, mCbScan.isChecked() ? "2" : null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_scan:
			 showCaptureActivity();
			break;
		case R.id.iv_menu:
			showOrHideTimeMenus();
			break;
		case R.id.tv_date:
			showDatePickerDialog();
			break;
		case R.id.tv_employee:
			showOrHideEmployeeMenus();
			break;
		default:
			break;
		}
	}

	private String getDateString(Date date) {
		return DateUtils.getDateString(date);
	}

	private void showDatePickerDialog() {
		Calendar c = Calendar.getInstance();
		DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
				final String newDate = getDateString(new Date(year - 1900, month, dayOfMonth));
				if (!mTVDate.getText().equals(newDate)) {
					mTVDate.setText(newDate);
					updateMainFragment();
				}
			}

		}, c.get(Calendar.YEAR), // 传入年份
				c.get(Calendar.MONTH), // 传入月份
				c.get(Calendar.DAY_OF_MONTH) // 传入天数
		);
		DatePicker datePicker = dialog.getDatePicker();
		if (mType == TYPE_HISTORY) {
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
			datePicker.setMaxDate(c.getTimeInMillis());
		} else if (mType == TYPE_INSPECT_SUPERVISE) {
			datePicker.setMaxDate(c.getTimeInMillis());
		} 
		dialog.show();
	}

	private void setCheckTimes(List<CheckTime> times) {
		mCheckTimes = times;
	}

	private void dismissTimeMenus() {
		if (mTimeMenus != null && mTimeMenus.isShowing()) {
			mTimeMenus.dismiss();
		}
	}

	private void dismissEmployeeMenus() {
		if (mEmployeeMenus != null && mEmployeeMenus.isShowing()) {
			mEmployeeMenus.dismiss();
		}
	}

	private void showOrHideEmployeeMenus() {
		if (mEmployeeMenus != null && mEmployeeMenus.isShowing()) {
			dismissEmployeeMenus();
		} else {
			getEmployeesAndShowMenu();

		}
	}

	private void getEmployeesAndShowMenu() {
		mProgressDialogUtils.show();
		StringRequest stringRequest = new StringRequest(Method.GET, getEmployeesUrl(), new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				mProgressDialogUtils.cancel();
				decodeEmployeesAndShowMenu(response);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				mProgressDialogUtils.cancel();
				mToast.show(R.string.netword_fail);
				Logger.debug(TAG, "getEmployeesAndShowMenu fail -->  " + error.toString());
			}
		});
		mQueue.add(stringRequest);

	}

	protected void decodeEmployeesAndShowMenu(String response) {
		@SuppressWarnings("unchecked")
		List<Employee> employees = (List<Employee>) DecodeJsonResponseUtils.decode(response, TAG,
				"decodeEmployeesAndShowMenu", getToast(), this, EmployeeListDTO.class);
		showEmployeeMenus(employees);
	}

	private String getEmployeesUrl() {
		return Constant.BASE_URL + String.format("/api/v1/index/%d/operators", getUserOperatorID());
	}

	private void showOrHideTimeMenus() {
		if (mTimeMenus != null && mTimeMenus.isShowing()) {
			dismissTimeMenus();
		} else {
			showTimeMenus();
		}
	}

	private void showTimeMenus() {
		if (mCheckTimes != null && mCheckTimes.size() > 0) {
			if (mTimeMenus == null) {
				initTimeMenu();
			}
			mTimeMenuAdapter.clear();
			mTimeMenuAdapter.addAll(mCheckTimes);
			mTimeMenus.showAsDropDown(findViewById(R.id.iv_menu), 10, 10);
		}
	}

	private void initTimeMenu() {
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.widget_list, null);
		ListView lvMenus = (ListView) view.findViewById(R.id.lv_list);
		if (mTimeMenuAdapter == null) {
			mTimeMenuAdapter = new ArrayAdapter<>(this, R.layout.widget_simple_list_item);
		}
		lvMenus.setAdapter(mTimeMenuAdapter);
		lvMenus.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mTVTime.setText(mTimeMenuAdapter.getItem(position).toString());
				mDetailFragment.setCheckTimeID(mTimeMenuAdapter.getItem(position).getId());
				dismissTimeMenus();
			}
		});
		mTimeMenus = new PopupWindow(view, getResources().getDimensionPixelSize(R.dimen.popup_menu_width),
				LayoutParams.WRAP_CONTENT);
		mTimeMenus.setBackgroundDrawable(new BitmapDrawable());
		mTimeMenus.setFocusable(true);
		mTimeMenus.setOutsideTouchable(true);
		mTimeMenus.update();
	}

	private void showEmployeeMenus(List<Employee> employees) {
		if (employees != null && employees.size() > 0) {
			if (mEmployeeMenus == null) {
				initEmployeeMenu();
			}
			mEmployeeMenuAdapter.clear();
			mEmployeeMenuAdapter.addAll(employees);
			mEmployeeMenus.showAsDropDown(findViewById(R.id.tv_employee), 10, 10);
		}
	}

	private void initEmployeeMenu() {
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.widget_list, null);
		ListView lvMenus = (ListView) view.findViewById(R.id.lv_list);
		if (mEmployeeMenuAdapter == null) {
			mEmployeeMenuAdapter = new ArrayAdapter<>(this, R.layout.widget_simple_list_item);
		}
		lvMenus.setAdapter(mEmployeeMenuAdapter);
		lvMenus.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mOperatorID = mEmployeeMenuAdapter.getItem(position).getOperator_id();
				updateMainFragment();
				mTVEmployee.setText(mEmployeeMenuAdapter.getItem(position).getName());
				dismissEmployeeMenus();
			}
		});
		mEmployeeMenus = new PopupWindow(view, getResources().getDimensionPixelSize(R.dimen.popup_menu_width),
				LayoutParams.WRAP_CONTENT);
		mEmployeeMenus.setBackgroundDrawable(new BitmapDrawable());
		mEmployeeMenus.setFocusable(true);
		mEmployeeMenus.setOutsideTouchable(true);
		mEmployeeMenus.update();
	}

	public int getOperatorId() {
		return mOperatorID;
	}

	private void showCaptureActivity() {
		Intent intent = new Intent(this, CaptureActivity.class);
		startActivityForResult(intent, SCAN_REQUEST_CODE);
	}

	// 用于判断是不是第一次进入页面
	private boolean isFirstResume = true;
	@Override
	protected void onResume() {
		super.onResume();
		if ((mType == TYPE_TODAY || mType == TYPE_HISTORY) && isFirstResume) {
			isFirstResume = false;
			updateMainFragment();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == SCAN_REQUEST_CODE) {
			String barcode = data.getStringExtra("codeString");
			decodeBarcodeFromNetWork(barcode);
		}
	}

	private void decodeBarcodeFromNetWork(final String barcode) {
		Logger.debug(TAG,  "decodeBarcodeFromNetWork" + " --> barcode："  + barcode);
		mProgressDialogUtils.show();
		StringRequest stringRequest = new StringRequest(Method.PUT, getBarcodeUrl(barcode),
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						mProgressDialogUtils.cancel();
						Log.d(TAG, "decodeBarcodeFromNetWork" + " --> " + response);
						try {
							ResponseDTO dto = JsonUtil.parseObject(response, SimpleResultDTO.class);
							if (dto.isValid()) {
								updateMainFragment();
							} else {
								getToast().show(getString(R.string.barcode_invalid));
								Logger.debug(TAG,  "decodeBarcodeFromNetWork" + " --> " + getString(R.string.barcode_invalid) + response);
							}
						} catch (Exception e) {
							getToast().show(getString(R.string.service_fail));
							e.printStackTrace();
							Logger.debug(TAG, "decodeBarcodeFromNetWork" + " --> " + getString(R.string.can_not_decode_data) + response);
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mProgressDialogUtils.cancel();
						mToast.show(R.string.netword_fail);
						Logger.debug(TAG, "decodeBarcodeFromNetWork fail -->  " + error.toString());
					}
				}) {

					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("orcode", String.valueOf(barcode));
						return map;
					}
			
		};
		mQueue.add(stringRequest);
	}

	private String getBarcodeUrl(String barcode) {
		return Constant.BASE_URL + String.format("/api/v1/index/%d/orcode", getUserOperatorID());
	}

	private void updateMainFragment() {
		mTVTime.setText(R.string.time_now);
		mDetailFragment.clear();
		mMainFragment.setParams(mTVDate.getText().toString(), mOperatorID);
	}

}
