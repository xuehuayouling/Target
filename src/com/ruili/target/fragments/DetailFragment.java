package com.ruili.target.fragments;

import java.util.List;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ruili.target.R;
import com.ruili.target.activitys.TargetDetailsActivity;
import com.ruili.target.activitys.TargetListActivity;
import com.ruili.target.adapters.DetailsFragmentAdapter;
import com.ruili.target.entity.CheckTime;
import com.ruili.target.entity.Subcategory;
import com.ruili.target.entity.SubcategoryListDTO;
import com.ruili.target.utils.Constant;
import com.ruili.target.utils.JsonUtil;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class DetailFragment extends ListFragment {
	private TargetListActivity mActivity;
	private DetailsFragmentAdapter mAdapter;
	private static final String TAG = DetailFragment.class.getSimpleName();
	private int mCategoryId;
	private int mCheckTimeId = CheckTime.CHECK_TIME_NULL;
	private String mDate;
	public void setActivity(TargetListActivity activity) {
		this.mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_details, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), TargetDetailsActivity.class);
		final Subcategory subcategory = (Subcategory) mAdapter.getItem(position);
		intent.putExtra(TargetDetailsActivity.KEY_SUBCATEGORY, subcategory.getIndex_log_id());
		startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new DetailsFragmentAdapter((TargetListActivity) getActivity(), null);
		this.setListAdapter(mAdapter);
	}

	public void updateData(int categoryId, int checktimeID, String date) {
		mCategoryId = categoryId;
		mCheckTimeId = checktimeID;
		mDate = date;
		reloadData();
	}
	
	public void setCheckTimeID(int checkTimeID) {
		mCheckTimeId = checkTimeID;
		reloadData();
	}
	
	private void reloadData() {
		mActivity.getProgressDialogUtils().show("");
		StringRequest stringRequest = new StringRequest(Method.GET, getSubCategoryUrl(),
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						mActivity.getProgressDialogUtils().cancel();
						decodeResponse(response);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mActivity.getProgressDialogUtils().cancel();
						mActivity.getToast().show(R.string.netword_fail);
					}
				});
		mActivity.getRequestQueue().add(stringRequest);
	}
 
	public void clear() {
		mAdapter.setSubcategories(null);
	}
	private void decodeResponse(String response) {
		Log.d(TAG, response);
		try {
			SubcategoryListDTO dto = JsonUtil.parseObject(response, SubcategoryListDTO.class);
			if (dto.isValid()) {
				List<Subcategory> subcategories = dto.getData();
				mAdapter.setSubcategories(subcategories);
			} else {
				mActivity.getToast().show(mActivity.getString(R.string.get_data_fail) + response);
			}
		} catch (Exception e) {
			mActivity.getToast().show(mActivity.getResources().getText(R.string.service_fail) + response);
			e.printStackTrace();
		}
	}
	private String getSubCategoryUrl() {
		String checktime = String.valueOf(mCheckTimeId);
		if (CheckTime.CHECK_TIME_NULL == mCheckTimeId) {
			checktime = null;
		}
		String url = Constant.BASE_URL + String.format("/api/v1/index/%d/%d/%s/%s/small_indexs",
				mCategoryId, mActivity.getUserOperatorID(), checktime, mDate);
		return url;
	}

}
