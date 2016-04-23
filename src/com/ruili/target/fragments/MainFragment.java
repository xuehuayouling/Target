package com.ruili.target.fragments;

import java.util.List;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ruili.target.R;
import com.ruili.target.activitys.TargetListActivity;
import com.ruili.target.adapters.MainFragmentAdapter;
import com.ruili.target.entity.Category;
import com.ruili.target.entity.CategoryListDTO;
import com.ruili.target.entity.DecodeBarCodeDTO;
import com.ruili.target.utils.Constant;
import com.ruili.target.utils.DecodeJsonResponseUtils;
import com.ruili.target.utils.Logger;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class MainFragment extends ListFragment {
	private TargetListActivity mActivity;
	private MainFragmentAdapter mAdapter;
	private static final String TAG = MainFragment.class.getSimpleName();
	private int mOperatorID = -1;
	private String mDate = null;
	/**
	 * 扫码返回的大类id
	 */
	private DecodeBarCodeDTO.IndexID mIndexID;

	public void setScanIndexID(DecodeBarCodeDTO.IndexID indexID) {
		mIndexID = indexID;
	}
	
	public void setActivity(TargetListActivity activity) {
		this.mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		final Category category = (Category) mAdapter.getItem(position);
		mAdapter.setSelectItem(position);
		mActivity.onMainListItemClick(category);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new MainFragmentAdapter(getActivity(), null);
		this.setListAdapter(mAdapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public void setParams(String date, int operatorID) {
		mOperatorID = operatorID;
		mDate = date;
		updateCategoryList();
	}

	private void updateCategoryList() {
		mActivity.getProgressDialogUtils().show("");
		StringRequest stringRequest = new StringRequest(Method.GET, getCategoryListUrl(),
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						mActivity.getProgressDialogUtils().cancel();
						decodeCategoryListResponse(response);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mActivity.getProgressDialogUtils().cancel();
						mActivity.getToast().show(R.string.netword_fail);
						Logger.debug(TAG, "getEmployeesAndShowMenu fail -->  " + error.toString());
					}
				});
		mActivity.getRequestQueue().add(stringRequest);
	}

	private void decodeCategoryListResponse(String response) {
		@SuppressWarnings("unchecked")
		List<Category> categories = (List<Category>) DecodeJsonResponseUtils.decode(response, TAG,
				"decodeCategoryListResponse", mActivity.getToast(), mActivity, CategoryListDTO.class);
		mAdapter.setCategories(categories);
		if (mIndexID != null && categories != null) {
			for (int i =0; i < categories.size(); i++) {
				if (categories.get(i).getId() == mIndexID.getBig_index_id()) {
					onListItemClick(null, null, i, -1);
					break;
				}
			}
			mIndexID = null;
		}
	}

	private String getCategoryListUrl() {
		String url = Constant.BASE_URL + String.format("/api/v1/index/%d/big_indexs", mOperatorID);
		if (mActivity.getType() == TargetListActivity.TYPE_INSPECT_SUPERVISE
				|| mActivity.getType() == TargetListActivity.TYPE_HISTORY) {
			url = Constant.BASE_URL + String.format("/api/v1/index/%s/%d/big_indexs", mDate, mOperatorID);
		}
		Logger.debug(TAG, "getCategoryListUrl -->  " + url);
		return url;
	}

	public void setDate(String date) {
		mDate = date;
		updateCategoryList();
	}

}
