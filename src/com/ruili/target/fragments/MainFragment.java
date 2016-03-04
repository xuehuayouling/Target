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
import com.ruili.target.entity.ResponseDTO;
import com.ruili.target.utils.Constant;
import com.ruili.target.utils.JsonUtil;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MainFragment extends ListFragment {
	private TargetListActivity mActivity;
	private MainFragmentAdapter mAdapter;

	public void setActivity(TargetListActivity activity) {
		this.mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mActivity.onMainListItemClick((Category) mAdapter.getItem(position));
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

	public void updateData() {
		mActivity.getProgressDialogUtils().show("");
		StringRequest stringRequest = new StringRequest(Method.GET, getCategoryUrl(),
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
						mActivity.getToast().show(error.toString());
					}
				});
		mActivity.getRequestQueue().add(stringRequest);
	}
	
	private void decodeResponse(String response) {
		try {
			ResponseDTO dto = JsonUtil.parseObject(response, ResponseDTO.class);
			if (dto.isValid()) {
				List<Category> categories = JsonUtil.parseSpecialArray(response, "data",Category.class);
				mAdapter.setCategories(categories);
			} else {
				mActivity.getToast().show(R.string.get_data_fail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mActivity.getToast().show(mActivity.getResources().getText(R.string.service_fail) + response);
		}
	}
	private String getCategoryUrl() {
		return Constant.BASE_URL + String.format("/api/v1/index/%d/big_indexs", mActivity.getUserOperatorID());
	}

}
