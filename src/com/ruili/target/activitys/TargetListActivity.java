package com.ruili.target.activitys;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ruili.target.R;
import com.ruili.target.entity.Category;
import com.ruili.target.entity.User;
import com.ruili.target.fragments.DetailFragment;
import com.ruili.target.fragments.MainFragment;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class TargetListActivity extends BaseActivity {

	public static final String TYPE_KEY = "type";
	public static final int TYPE_TODAY = 0;
	public static final int TYPE_HISTORY = 1;
	public static final int TYPE_INSPECT_SUPERVISE = 2;
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
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.widget_date_picker);
		TextView search = (TextView) actionBar.getCustomView().findViewById(R.id.tv_date_picker);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(TargetListActivity.this, "Search triggered", Toast.LENGTH_LONG).show();
			}
		});
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		switch (getUserType()) {
		case User.TYPE_QC:
			
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			break;
		case R.id.action_scan:
			mMainFragment.updateData();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
}
