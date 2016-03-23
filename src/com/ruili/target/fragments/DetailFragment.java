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
import com.ruili.target.utils.DecodeJsonResponseUtils;
import com.ruili.target.utils.Logger;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class DetailFragment extends ListFragment {
	private TargetListActivity mActivity;
	private DetailsFragmentAdapter mAdapter;
	private static final String TAG = DetailFragment.class.getSimpleName();
	private int mCategoryId = -1;
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
		intent.putExtra(TargetDetailsActivity.KEY_TYPE, mActivity.getType());
		startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new DetailsFragmentAdapter((TargetListActivity) getActivity(), null);
		this.setListAdapter(mAdapter);
	}

	public void updateSubcategoryList(int categoryId, int checktimeID, String date) {
		mCategoryId = categoryId;
		mCheckTimeId = checktimeID;
		mDate = date;
		loadSubcategoryList();
	}

	public void setCheckTimeID(int checkTimeID) {
		mCheckTimeId = checkTimeID;
		loadSubcategoryList();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mCategoryId != -1) {
			loadSubcategoryList();
		}
	}

	private void loadSubcategoryList() {
		if (mCategoryId == -1 || mActivity.getUserOperatorID() < 1) {
			return;
		}
		mActivity.getProgressDialogUtils().show("");
		StringRequest stringRequest = new StringRequest(Method.GET, getSubCategoryUrl(),
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						mActivity.getProgressDialogUtils().cancel();
						decodeSubcategoryListResponse(response);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mActivity.getProgressDialogUtils().cancel();
						mActivity.getToast().show(R.string.netword_fail);
						Logger.debug(TAG, "loadSubcategoryList fail -->   " + error.toString());
					}
				});
		mActivity.getRequestQueue().add(stringRequest);
	}

	public void clear() {
		mAdapter.setSubcategories(null);
	}

	@SuppressWarnings("unchecked")
	private void decodeSubcategoryListResponse(String response) {
		List<Subcategory> subcategories = (List<Subcategory>) DecodeJsonResponseUtils.decode(response, TAG,
				"decodeSubcategoryListResponse", mActivity.getToast(), mActivity, SubcategoryListDTO.class);
		mAdapter.setSubcategories(subcategories);
	}

	private String getSubCategoryUrl() {
		String checktime = String.valueOf(mCheckTimeId);
		if (CheckTime.CHECK_TIME_NULL == mCheckTimeId) {
			if (mActivity.getType() == TargetListActivity.TYPE_TODAY) {
				checktime = "0";
			} else {
				checktime = null;
			}
		}
		int operatoryId = mActivity.getUserOperatorID();
		if (mActivity.getType() == TargetListActivity.TYPE_INSPECT_SUPERVISE) {
			operatoryId = mActivity.getOperatorId();
		}
		String url = Constant.BASE_URL
				+ String.format("/api/v1/index/%d/%d/%s/%s/small_indexs", mCategoryId, operatoryId, checktime, mDate);
		Logger.debug(TAG, "getSubCategoryUrl -->  " + url);
		return url;
	}

}
