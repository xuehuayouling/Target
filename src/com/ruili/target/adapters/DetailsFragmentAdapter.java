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
import com.ruili.target.activitys.TargetDetailsActivity;
import com.ruili.target.activitys.TargetListActivity;
import com.ruili.target.entity.Subcategory;
import com.ruili.target.entity.SubcategoryDTO;
import com.ruili.target.utils.Constant;
import com.ruili.target.utils.JsonUtil;
import com.ruili.target.utils.Logger;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
		return (mSubcategories.size() + 1) / 2;
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
		if (null == convertView) {
			view = mInflater.inflate(R.layout.fragment_details_list_item, parent, false);
		} else {
			view = convertView;
		}
		initView(view);
		setViewData(position, view);
		setViewData2(position, view);
		return view;
	}

	private void setViewData(int position, View view) {
		final Subcategory subcategory = mSubcategories.get(position * 2);
		view.findViewById(R.id.ll_details_list_item).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mActivity.getType() == TargetListActivity.TYPE_TODAY && subcategory.getEdit_type().equals("0")) {
				} else {
					onListItemClick(subcategory);
				}
			}
		});
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.viewState.setBackgroundResource(subcategory.getStateResourceID());
		final View viewState = holder.viewState;
		holder.tvTitle.setText(subcategory.getSmall_index_name());
		if (Subcategory.INDEX_TYPE_YESNO == subcategory.getIndex_type()) {
			holder.llScore.setVisibility(View.GONE);
			holder.rgState.setVisibility(View.VISIBLE);
			if (mActivity.getType() == TargetListActivity.TYPE_INSPECT_SUPERVISE
					|| mActivity.getType() == TargetListActivity.TYPE_HISTORY
					|| (mActivity.getType() == TargetListActivity.TYPE_TODAY
							&& subcategory.getEdit_type().equals("0"))) {
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
			holder.rgState.findViewById(R.id.rbtn_yes).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (subcategory.getIndex_complete() != Subcategory.INDEX_COMPLETE_YES) {
						subcategory.setIndex_complete(Subcategory.INDEX_COMPLETE_YES);
						updateSubcategory(subcategory, viewState);
					}
				}
			});
			holder.rgState.findViewById(R.id.rbtn_no).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (subcategory.getIndex_complete() != Subcategory.INDEX_COMPLETE_NO) {
						Intent intent = new Intent(mActivity, TargetDetailsActivity.class);
						intent.putExtra(TargetDetailsActivity.KEY_SUBCATEGORY, subcategory.getIndex_log_id());
						intent.putExtra(TargetDetailsActivity.KEY_SUBCATEGORY_COMPLETE, Subcategory.INDEX_COMPLETE_NO);
						intent.putExtra(TargetDetailsActivity.KEY_TYPE, mActivity.getType());
						mActivity.startActivity(intent);
					}
				}
			});
		} else if (Subcategory.INDEX_TYPE_SCORE == subcategory.getIndex_type()) {
			holder.rgState.setVisibility(View.GONE);
			holder.llScore.setVisibility(View.VISIBLE);
			if (mActivity.getType() == TargetListActivity.TYPE_INSPECT_SUPERVISE
					|| mActivity.getType() == TargetListActivity.TYPE_HISTORY
					|| (mActivity.getType() == TargetListActivity.TYPE_TODAY
							&& subcategory.getEdit_type().equals("0"))) {
				holder.rbarScore.setEnabled(false);
			}
			int score = 0;
			try {
				score = Integer.valueOf(subcategory.getIndex_score());
			} catch (NumberFormatException e) {
			}
			holder.rbarScore.setRating(score);
			holder.rbarScore.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
					if (fromUser) {
						if (rating < 3) {
							Intent intent = new Intent(mActivity, TargetDetailsActivity.class);
							intent.putExtra(TargetDetailsActivity.KEY_SUBCATEGORY, subcategory.getIndex_log_id());
							intent.putExtra(TargetDetailsActivity.KEY_SUBCATEGORY_SCORE, String.valueOf((int) rating));
							intent.putExtra(TargetDetailsActivity.KEY_TYPE, mActivity.getType());
							mActivity.startActivity(intent);
						} else {
							subcategory.setIndex_score(String.valueOf((int) rating));
							updateSubcategory(subcategory, viewState);
						}
					}
				}
			});
		}
	}

	private void setViewData2(int position, View view) {
		if (mSubcategories.size() < (position * 2 + 2)) {
			view.findViewById(R.id.ll_details_list_item2).setVisibility(View.INVISIBLE);
			return;
		} else {
			view.findViewById(R.id.ll_details_list_item2).setVisibility(View.VISIBLE);
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		final Subcategory subcategory = mSubcategories.get(position * 2 + 1);
		view.findViewById(R.id.ll_details_list_item2).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mActivity.getType() == TargetListActivity.TYPE_TODAY && subcategory.getEdit_type().equals("0")) {
				} else {
					onListItemClick(subcategory);
				}
			}
		});
		holder.viewState2.setBackgroundResource(subcategory.getStateResourceID());
		final View viewState = holder.viewState2;
		holder.tvTitle2.setText(subcategory.getSmall_index_name());
		if (Subcategory.INDEX_TYPE_YESNO == subcategory.getIndex_type()) {
			holder.llScore2.setVisibility(View.GONE);
			holder.rgState2.setVisibility(View.VISIBLE);
			if (mActivity.getType() == TargetListActivity.TYPE_INSPECT_SUPERVISE
					|| mActivity.getType() == TargetListActivity.TYPE_HISTORY
					|| (mActivity.getType() == TargetListActivity.TYPE_TODAY
							&& subcategory.getEdit_type().equals("0"))) {
				holder.rgState2.findViewById(R.id.rbtn_yes2).setEnabled(false);
				holder.rgState2.findViewById(R.id.rbtn_no2).setEnabled(false);
			}
			if (subcategory.getIndex_complete() == Subcategory.INDEX_COMPLETE_YES) {
				holder.rgState2.check(R.id.rbtn_yes2);
			} else if (subcategory.getIndex_complete() == Subcategory.INDEX_COMPLETE_NO) {
				holder.rgState2.check(R.id.rbtn_no2);
			} else {
				holder.rgState2.clearCheck();
			}
			holder.rgState2.findViewById(R.id.rbtn_yes2).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (subcategory.getIndex_complete() != Subcategory.INDEX_COMPLETE_YES) {
						subcategory.setIndex_complete(Subcategory.INDEX_COMPLETE_YES);
						updateSubcategory(subcategory, viewState);
					}
				}
			});
			holder.rgState2.findViewById(R.id.rbtn_no2).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (subcategory.getIndex_complete() != Subcategory.INDEX_COMPLETE_NO) {
						Intent intent = new Intent(mActivity, TargetDetailsActivity.class);
						intent.putExtra(TargetDetailsActivity.KEY_SUBCATEGORY, subcategory.getIndex_log_id());
						intent.putExtra(TargetDetailsActivity.KEY_SUBCATEGORY_COMPLETE, Subcategory.INDEX_COMPLETE_NO);
						intent.putExtra(TargetDetailsActivity.KEY_TYPE, mActivity.getType());
						mActivity.startActivity(intent);
					}
				}
			});
		} else if (Subcategory.INDEX_TYPE_SCORE == subcategory.getIndex_type()) {
			holder.rgState2.setVisibility(View.GONE);
			holder.llScore2.setVisibility(View.VISIBLE);
			if (mActivity.getType() == TargetListActivity.TYPE_INSPECT_SUPERVISE
					|| mActivity.getType() == TargetListActivity.TYPE_HISTORY
					|| (mActivity.getType() == TargetListActivity.TYPE_TODAY
							&& subcategory.getEdit_type().equals("0"))) {
				holder.rbarScore2.setEnabled(false);
			}
			int score = 0;
			try {
				score = Integer.valueOf(subcategory.getIndex_score());
			} catch (NumberFormatException e) {
			}
			holder.rbarScore2.setRating(score);
			holder.rbarScore2.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
					if (fromUser) {
						if (rating < 3) {
							Intent intent = new Intent(mActivity, TargetDetailsActivity.class);
							intent.putExtra(TargetDetailsActivity.KEY_SUBCATEGORY, subcategory.getIndex_log_id());
							intent.putExtra(TargetDetailsActivity.KEY_SUBCATEGORY_SCORE, String.valueOf((int) rating));
							intent.putExtra(TargetDetailsActivity.KEY_TYPE, mActivity.getType());
							mActivity.startActivity(intent);
						} else {
							subcategory.setIndex_score(String.valueOf((int) rating));
							updateSubcategory(subcategory, viewState);
						}
					}
				}
			});
		}
	}

	private void initView(View view) {
		ViewHolder holder = new ViewHolder();
		holder.viewState = view.findViewById(R.id.view_state);
		holder.rgState = (RadioGroup) view.findViewById(R.id.rg_state);
		holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
		holder.rbarScore = (RatingBar) view.findViewById(R.id.rbar_score);
		holder.llScore = (LinearLayout) view.findViewById(R.id.ll_score);
		holder.viewState2 = view.findViewById(R.id.view_state2);
		holder.rgState2 = (RadioGroup) view.findViewById(R.id.rg_state2);
		holder.tvTitle2 = (TextView) view.findViewById(R.id.tv_title2);
		holder.rbarScore2 = (RatingBar) view.findViewById(R.id.rbar_score2);
		holder.llScore2 = (LinearLayout) view.findViewById(R.id.ll_score2);
		view.setTag(holder);
	}

	private void updateSubcategory(final Subcategory subcategory, final View view) {
		mActivity.getProgressDialogUtils().show();
		StringRequest stringRequest = new StringRequest(Method.PUT,
				getUpdateSubCategoryUrl(subcategory.getIndex_log_id()), new Response.Listener<String>() {

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

	public void onListItemClick(Subcategory subcategory) {
		Intent intent = new Intent(mActivity, TargetDetailsActivity.class);
		intent.putExtra(TargetDetailsActivity.KEY_SUBCATEGORY, subcategory.getIndex_log_id());
		intent.putExtra(TargetDetailsActivity.KEY_TYPE, mActivity.getType());
		mActivity.startActivity(intent);
	}

	class ViewHolder {
		View viewState;
		LinearLayout llScore;
		RatingBar rbarScore;
		RadioGroup rgState;
		TextView tvTitle;
		View viewState2;
		LinearLayout llScore2;
		RatingBar rbarScore2;
		RadioGroup rgState2;
		TextView tvTitle2;
	}
}
