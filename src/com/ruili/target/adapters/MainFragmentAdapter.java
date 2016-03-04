package com.ruili.target.adapters;

import java.util.ArrayList;
import java.util.List;

import com.ruili.target.R;
import com.ruili.target.entity.Category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MainFragmentAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Category> mCategories;

	public MainFragmentAdapter(Context context, List<Category> categories) {
		super();
		this.mContext = context;
		if (null == categories) {
			mCategories = new ArrayList<>();
		} else {
			mCategories = categories;
		}
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setCategories(List<Category> categories) {
		this.mCategories = categories;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mCategories.size();
	}

	@Override
	public Object getItem(int position) {
		return mCategories.get(position);
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
			holder.btnNo = (Button) view.findViewById(R.id.btn_id);
			holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
			holder.tvSummary = (TextView) view.findViewById(R.id.tv_summary);
			view.setTag(holder);
		} else {
			view = convertView;
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		final Category category = mCategories.get(position);
		holder.tvTitle.setText(category.getName());
		holder.btnNo.setText(String.valueOf(category.getId()));
		return view;
	}

	class ViewHolder {
		Button btnNo;
		TextView tvTitle;
		TextView tvSummary;
	}
}
