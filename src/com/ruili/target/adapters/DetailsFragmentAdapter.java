package com.ruili.target.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ruili.target.R;
import com.ruili.target.activitys.TargetListActivity;
import com.ruili.target.entity.PicUrl;
import com.ruili.target.entity.Subcategory;
import com.ruili.target.utils.Constant;
import com.ruili.target.utils.Logger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class DetailsFragmentAdapter extends BaseAdapter {
	private static final String TAG = DetailsFragmentAdapter.class.getSimpleName();
	private LayoutInflater mInflater;
	private List<Subcategory> mSubcategories;
	private TargetListActivity mActivity;

	public DetailsFragmentAdapter(TargetListActivity activity, List<Subcategory> subcategories) {
		super();
		mActivity = activity;
		if (null == subcategories) {
			mSubcategories = new ArrayList<>();
		} else {
			mSubcategories = subcategories;
		}
		mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setSubcategories(List<Subcategory> subcategories) {
		if (subcategories == null) {
			subcategories = new ArrayList<>();
		}
		this.mSubcategories = subcategories;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mSubcategories.size();
	}

	@Override
	public Object getItem(int position) {
		return mSubcategories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		final Subcategory subcategory = mSubcategories.get(position);
		if (null == convertView) {
			view = mInflater.inflate(R.layout.fragment_details_list_item, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.viewState = view.findViewById(R.id.view_state);
			holder.rgState = (RadioGroup) view.findViewById(R.id.rg_state);
			holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
			holder.rbarScore = (RatingBar) view.findViewById(R.id.rbar_score);
			holder.llScore = (LinearLayout) view.findViewById(R.id.ll_score);
			view.setTag(holder);
		} else {
			view = convertView;
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.viewState.setBackgroundResource(subcategory.getStateResourceID());
		holder.tvTitle.setText(subcategory.getSmall_index_name());
		if (Subcategory.INDEX_TYPE_YESNO == subcategory.getIndex_type()) {
			holder.llScore.setVisibility(View.GONE);
			holder.rgState.setVisibility(View.VISIBLE);
			if (subcategory.getIndex_complete() == Subcategory.INDEX_COMPLETE_YES) {
				holder.rgState.check(R.id.rbtn_yes);
			} else if (subcategory.getIndex_complete() == Subcategory.INDEX_COMPLETE_NO) {
				holder.rgState.check(R.id.rbtn_no);
			} else {
				holder.rgState.clearCheck();
			}
			holder.rgState.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch (checkedId) {
					case R.id.rbtn_yes:
						subcategory.setIndex_complete(Subcategory.INDEX_COMPLETE_YES);
						break;
					case R.id.rbtn_no:
						subcategory.setIndex_complete(Subcategory.INDEX_COMPLETE_NO);
						break;
					default:
						break;
					}
					updateSubcategory(subcategory);
				}
			});
		} else if (Subcategory.INDEX_TYPE_SCORE == subcategory.getIndex_type()) {
			holder.rgState.setVisibility(View.GONE);
			holder.llScore.setVisibility(View.VISIBLE);
			int score;
			try {
				score = Integer.valueOf(subcategory.getIndex_score());
				holder.rbarScore.setRating(score);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			holder.rbarScore.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				
				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
					if (fromUser) {
						subcategory.setIndex_score(String.valueOf((int)rating));
						updateSubcategory(subcategory);
					}
				}
			});
		} else {
			return null;
		}
		
		return view;
	}

	private void updateSubcategory(final Subcategory subcategory) {
		mActivity.getProgressDialogUtils().show();
		StringRequest stringRequest = new StringRequest(Method.PUT, getUpdateSubCategoryUrl(subcategory.getIndex_log_id()),
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						mActivity.getProgressDialogUtils().cancel();
						mActivity.getToast().show(response);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.debug(TAG, error.toString());
						mActivity.getProgressDialogUtils().cancel();
						mActivity.getToast().show(R.string.netword_fail);
					}
				}) {

					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("index_complete",  String.valueOf(subcategory.getIndexComplete()));
						map.put("index_score", String.valueOf(subcategory.getIndex_score()));
						map.put("index_remark", String.valueOf(subcategory.getIndex_remark()));
						map.put("index_pic", String.valueOf(subcategory.getIndexPics()));
						return map;
					}
			
		};
		mActivity.getRequestQueue().add(stringRequest);
	}
	
	private String getUpdateSubCategoryUrl(int indexLogId) {
		return Constant.BASE_URL + String.format("/api/v1/index/%d/index_log", indexLogId);
	}
	
	class ViewHolder {
		View viewState;
		LinearLayout llScore;
		RatingBar rbarScore;
		RadioGroup rgState;
		TextView tvTitle;
	}
}
