package com.ruili.target.activitys;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.history.HistoryItem;
import com.ruili.target.R;
import com.ruili.target.entity.Category;
import com.ruili.target.fragments.DetailFragment;
import com.ruili.target.fragments.MainFragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		switch (getIntent().getIntExtra(TYPE_KEY, -1)) {
		case TYPE_TODAY:
		case TYPE_HISTORY:
		case TYPE_INSPECT_SUPERVISE:
			break;
		default:
			break;
		}
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
		mDetailFragment.updateData(1, 1, "2016-03-03");;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_scan:
			showCaptureActivity();
//			mMainFragment.updateData();
			break;
		default:
			break;
		}		
	}
	
	private void showCaptureActivity() {
		Intent intent = new Intent(this, CaptureActivity.class);
		startActivityForResult(intent, SCAN_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == SCAN_REQUEST_CODE) {
		      String url = data.getStringExtra("codeString");
		      mToast.show(url);
		    }
	}
	
	
}
