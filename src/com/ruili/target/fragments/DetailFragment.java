package com.ruili.target.fragments;

import java.util.List;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ruili.target.R;
import com.ruili.target.activitys.TargetListActivity;
import com.ruili.target.adapters.DetailsFragmentAdapter;
import com.ruili.target.entity.ResponseDTO;
import com.ruili.target.entity.Subcategory;
import com.ruili.target.utils.Constant;
import com.ruili.target.utils.JsonUtil;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class DetailFragment extends ListFragment {
	private TargetListActivity mActivity;
	private DetailsFragmentAdapter mAdapter;

	public void setActivity(TargetListActivity activity) {
		this.mActivity = activity;
	}

	String[] cities = {};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_details, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new DetailsFragmentAdapter(getActivity(), null);
		this.setListAdapter(mAdapter);
	}

	public void updateData(int categoryId, int checktimeID, String date) {
		mActivity.getProgressDialogUtils().show("");
		StringRequest stringRequest = new StringRequest(Method.GET, getSubCategoryUrl(categoryId, checktimeID, date),
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

	private void decodeResponse(String response) {
		try {
			ResponseDTO dto = JsonUtil.parseObject(response, ResponseDTO.class);
			if (dto.isValid()) {
				List<Subcategory> subcategories = JsonUtil.parseSpecialArray(response, "data", Subcategory.class);
				mAdapter.setSubcategories(subcategories);
			} else {
				mActivity.getToast().show(R.string.get_data_fail);
			}
		} catch (Exception e) {
			mActivity.getToast().show(mActivity.getResources().getText(R.string.service_fail) + response);
			e.printStackTrace();
		}
	}
	private String getSubCategoryUrl(int categoryId, int checktimeID, String date) {
		String url = Constant.BASE_URL + String.format("/api/v1/index/%d/%d/%d/%s/small_indexs",
				categoryId, mActivity.getUserOperatorID(), checktimeID, date);
		return url;
	}

}
