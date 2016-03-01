package com.ruili.target.adapters;

import java.util.ArrayList;
import java.util.List;

import com.ruili.target.R;
import com.ruili.target.entity.Entity1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsFragmentAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Entity1> mEntities;

	public DetailsFragmentAdapter(Context context, List<Entity1> entities) {
		super();
		this.mContext = context;
		if (null == entities) {
			mEntities = new ArrayList<>();
		} else {
			mEntities = entities;
		}
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mEntities.size();
	}

	@Override
	public Object getItem(int position) {
		return mEntities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Entity1 entity = mEntities.get(position);
		return entity.getView(convertView, parent, mInflater);
	}

	class ViewHolder {
		ImageView ivNo;
		TextView tvTitle;
		TextView tvSummary;
	}
}
