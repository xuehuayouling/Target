package com.ruili.target.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ruili.target.R;
import com.ruili.target.activitys.TargetListActivity;
import com.ruili.target.entity.Subcategory;
import com.ruili.target.entity.SubcategoryDTO;
import com.ruili.target.utils.Constant;
import com.ruili.target.utils.JsonUtil;
import com.ruili.target.utils.Logger;

import android.content.Context;
import android.util.Log;
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
		final View viewState = holder.viewState;
		holder.tvTitle.setText(subcategory.getSmall_index_name());
		if (Subcategory.INDEX_TYPE_YESNO == subcategory.getIndex_type()) {
			holder.llScore.setVisibility(View.GONE);
			holder.rgState.setVisibility(View.VISIBLE);
			if (mActivity.getType() == TargetListActivity.TYPE_INSPECT_SUPERVISE) {
				holder.rgState.findViewById(R.id.rbtn_yes).setEnabled(false);
				holder.rgState.findViewById(R.id.rbtn_no).setEnabled(false);
			}
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
					updateSubcategory(subcategory, viewState);
				}
			});
		} else if (Subcategory.INDEX_TYPE_SCORE == subcategory.getIndex_type()) {
			holder.rgState.setVisibility(View.GONE);
			holder.llScore.setVisibility(View.VISIBLE);
			if (mActivity.getType() == TargetListActivity.TYPE_INSPECT_SUPERVISE) {
				holder.rbarScore.setEnabled(false);
			}
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
						updateSubcategory(subcategory, viewState);
					}
				}
			});
		} else {
			return null;
		}
		
		return view;
	}

	private void updateSubcategory(final Subcategory subcategory, final View view) {
		mActivity.getProgressDialogUtils().show();
		StringRequest stringRequest = new StringRequest(Method.PUT, getUpdateSubCategoryUrl(subcategory.getIndex_log_id()),
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Logger.debug(TAG, "updateSubcategory success -->   " + response);
						mActivity.getProgressDialogUtils().cancel();
						decodeResponse(response, view);
						
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.debug(TAG, "updateSubcategory success -->   " + error.toString());
						mActivity.getProgressDialogUtils().cancel();
						mActivity.getToast().show(R.string.netword_fail);
					}
				}) {

					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						return subcategory.getUpdataParams(TAG);
					}
			
		};
		mActivity.getRequestQueue().add(stringRequest);
	}
	
	private void decodeResponse(String response, View view) {
		Log.d(TAG, response);
		try {
			SubcategoryDTO dto = JsonUtil.parseObject(response, SubcategoryDTO.class);
			if (dto.isValid()) {
				Subcategory subcategory = dto.getData();
				view.setBackgroundResource(subcategory.getStateResourceID());
			} else {
				mActivity.getToast().show(mActivity.getString(R.string.get_data_fail) + response);
			}
		} catch (Exception e) {
			mActivity.getToast().show(mActivity.getResources().getText(R.string.service_fail) + response);
			e.printStackTrace();
		}
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
