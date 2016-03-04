package com.ruili.target.adapters;

import java.util.ArrayList;
import java.util.List;

import com.ruili.target.R;
import com.ruili.target.entity.Subcategory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class DetailsFragmentAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Subcategory> mSubcategories;

	public DetailsFragmentAdapter(Context context, List<Subcategory> subcategories) {
		super();
		if (null == subcategories) {
			mSubcategories = new ArrayList<>();
		} else {
			mSubcategories = subcategories;
		}
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setSubcategories(List<Subcategory> mSubcategories) {
		this.mSubcategories = mSubcategories;
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
			view.setTag(holder);
		} else {
			view = convertView;
		}
		ViewHolder holder = new ViewHolder();
		holder = (ViewHolder) view.getTag();
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
		} else if (Subcategory.INDEX_TYPE_SCORE == subcategory.getIndex_type()) {
			
		} else {
			return null;
		}
		
		return view;
	}

	class ViewHolder {
		View viewState;
		RadioGroup rgState;
		ImageView ivNo;
		TextView tvTitle;
		TextView tvSummary;
	}
}
