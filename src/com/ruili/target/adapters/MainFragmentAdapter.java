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

public class MainFragmentAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Entity1> mEntities;

	public MainFragmentAdapter(Context context, List<Entity1> entities) {
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
		View view;
		if (null == convertView) {
			view = mInflater.inflate(R.layout.fragment_main_list_item, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.ivNo = (ImageView) view.findViewById(R.id.icon);
			holder.tvTitle = (TextView) view.findViewById(R.id.title);
			holder.tvSummary = (TextView) view.findViewById(R.id.summary);
			view.setTag(holder);
		} else {
			view = convertView;
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		final Entity1 entity = mEntities.get(position);
		holder.tvTitle.setText(entity.getTitle());
		return view;
	}

	class ViewHolder {
		ImageView ivNo;
		TextView tvTitle;
		TextView tvSummary;
	}
}
