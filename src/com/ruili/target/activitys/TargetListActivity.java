package com.ruili.target.activitys;

import java.util.List;

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
import com.ruili.target.fragments.DetailFragment;
import com.ruili.target.fragments.MainFragment;
import com.ruili.target.utils.Constant;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

public class TargetListActivity extends BaseActivity implements OnClickListener {

	public static final String TYPE_KEY = "type";
	public static final int TYPE_TODAY = 0;
	public static final int TYPE_HISTORY = 1;
	public static final int TYPE_INSPECT_SUPERVISE = 2;
	private static final int SCAN_REQUEST_CODE = 1;
	private RequestQueue mQueue;
	private MainFragment mMainFragment;
	private DetailFragment mDetailFragment;
	private List<CheckTime> mCheckTimes;
	// 今日指标、历史指标、监督管理
	private int mType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mType = getIntent().getIntExtra(TYPE_KEY, -1);
		initRequestQueue();
		setContentView(R.layout.activity_target_list);
		FragmentManager manager = getFragmentManager();
		mMainFragment = (MainFragment) manager.findFragmentById(R.id.fragment_main);
		mMainFragment.setActivity(this);
		mDetailFragment = (DetailFragment) manager.findFragmentById(R.id.fragment_detail);
		mDetailFragment.setActivity(this);
		initActionbar();
	}

	private void initActionbar() {
		ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
		ImageView ivMenu = (ImageView) findViewById(R.id.iv_menu);
		TextView tvDate = (TextView) findViewById(R.id.tv_date);
		TextView tvEmployee = (TextView) findViewById(R.id.tv_employee);
		SearchView svSearch = (SearchView) findViewById(R.id.sv_search);
		TextView tvScan = (TextView) findViewById(R.id.tv_scan);
		ivBack.setOnClickListener(this);
		ivMenu.setOnClickListener(this);
		tvEmployee.setOnClickListener(this);
		tvDate.setOnClickListener(this);
		svSearch.setOnClickListener(this);
		tvScan.setOnClickListener(this);
		switch (mType) {
		case TYPE_TODAY:
			tvDate.setVisibility(View.GONE);
			tvEmployee.setVisibility(View.GONE);
			svSearch.setVisibility(View.GONE);
			break;
		case TYPE_HISTORY:
		case TYPE_INSPECT_SUPERVISE:
			break;
		default:
			break;
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

	public void onMainListItemClick(Category category) {
		mDetailFragment.updateData(1, -1, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_scan:
			// showCaptureActivity();
			mDetailFragment.clear();
			mMainFragment.updateData();
			break;
		case R.id.iv_menu:
			showOrHideTimeMenus();
			break;
		default:
			break;
		}
	}

	public void setCheckTimes(List<CheckTime> times) {
		mCheckTimes = times;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dismissTimeMenus();
		return super.onTouchEvent(event);
	}

	private void dismissTimeMenus() {
		if (mTimeMenus != null && mTimeMenus.isShowing()) {
			mTimeMenus.dismiss();
		}
	}

	private PopupWindow mTimeMenus;
	private ArrayAdapter<CheckTime> mTimeMenuAdapter;

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
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = layoutInflater.inflate(R.layout.widget_list, null);
				ListView lvMenus = (ListView) view.findViewById(R.id.lv_list);
				if (mTimeMenuAdapter == null) {
					mTimeMenuAdapter = new ArrayAdapter<>(this, R.layout.widget_simple_list_item);
				}
				mTimeMenuAdapter.clear();
				mTimeMenuAdapter.addAll(mCheckTimes);
				lvMenus.setAdapter(mTimeMenuAdapter);
				lvMenus.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
					}
				});
				mTimeMenus = new PopupWindow(view, getResources().getDimensionPixelSize(R.dimen.popup_menu_width), LayoutParams.WRAP_CONTENT);
				mTimeMenus.setBackgroundDrawable(new BitmapDrawable());
				mTimeMenus.setFocusable(true);
				mTimeMenus.setOutsideTouchable(true);
				mTimeMenus.update();
			} else {
				mTimeMenuAdapter.clear();
				mTimeMenuAdapter.addAll(mCheckTimes);
			}
			mTimeMenus.showAsDropDown(findViewById(R.id.iv_menu), 10, 10);
		}
	}

	private void showCaptureActivity() {
		Intent intent = new Intent(this, CaptureActivity.class);
		startActivityForResult(intent, SCAN_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == SCAN_REQUEST_CODE) {
			String barcode = data.getStringExtra("codeString");
			decodeBarcode(barcode);
		}
	}

	private void decodeBarcode(String barcode) {
		mProgressDialogUtils.show();
		StringRequest stringRequest = new StringRequest(Method.GET, getBarcodeUrl(barcode),
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						mProgressDialogUtils.cancel();
						mToast.show(response);
						mMainFragment.updateData();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mProgressDialogUtils.cancel();
						mToast.show(R.string.netword_fail);
					}
				});
		mQueue.add(stringRequest);
	}

	private String getBarcodeUrl(String barcode) {
		return Constant.BASE_URL + String.format("/api/v1/index/%d/%s", getUserOperatorID(), barcode);
	}

}
