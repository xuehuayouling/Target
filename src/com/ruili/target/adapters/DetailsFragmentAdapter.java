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
			if (Subcategory.INDEX_TYPE_YESNO == subcategory.getIndex_type()) {
				view = mInflater.inflate(R.layout.fragment_details_list_item1, parent, false);
			} else if (Subcategory.INDEX_TYPE_SCORE == subcategory.getIndex_type()) {
				view = mInflater.inflate(R.layout.fragment_details_list_item2, parent, false);
			} else {
				return null;
			}
			ViewHolder holder = new ViewHolder();
			holder.viewState = view.findViewById(R.id.view_state);
			holder.rgState = (RadioGroup) view.findViewById(R.id.rg_state);
			holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
			holder.rbarScore = (RatingBar) view.findViewById(R.id.rbar_score);
			view.setTag(holder);
		} else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			if (holder.rgState == null && Subcategory.INDEX_TYPE_YESNO == subcategory.getIndex_type()) {
				view = mInflater.inflate(R.layout.fragment_details_list_item1, parent, false);
			} else if (holder.rbarScore == null && Subcategory.INDEX_TYPE_SCORE == subcategory.getIndex_type()){
				view = mInflater.inflate(R.layout.fragment_details_list_item2, parent, false);
			} else {
				view = convertView;
			}
			holder.viewState = view.findViewById(R.id.view_state);
			holder.rgState = (RadioGroup) view.findViewById(R.id.rg_state);
			holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
			holder.rbarScore = (RatingBar) view.findViewById(R.id.rbar_score);
			view.setTag(holder);
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.viewState.setBackgroundResource(subcategory.getStateResourceID());
		holder.tvTitle.setText(subcategory.getSmall_index_name());
		if (Subcategory.INDEX_TYPE_YESNO == subcategory.getIndex_type()) {
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
					subcategory.setIndex_score(String.valueOf((int)rating));
					updateSubcategory(subcategory);
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
						map.put("index_complete", String.valueOf(subcategory.getIndex_complete()));
						map.put("index_score", String.valueOf(subcategory.getIndex_score()));
						map.put("index_remark", String.valueOf(subcategory.getIndex_remark()));
						List<PicUrl> picUrls = subcategory.getIndex_pic();
						String picUrlString = "";
						if (picUrls != null) {
							for (PicUrl picUrl : picUrls) {
								if (picUrl.equals("")) {
									picUrlString += picUrl.getPic_url();
								} else {
									picUrlString += ";" + picUrl.getPic_url();
								}
							}
						}
						map.put("index_pic", picUrlString);
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
		RatingBar rbarScore;
		RadioGroup rgState;
		TextView tvTitle;
	}
}
