package com.ruili.target.entity;

import com.ruili.target.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Entity1 {

	protected String id;
	protected String title;
	protected String summary;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public View getView(View convertView, ViewGroup parent, LayoutInflater inflater) {
		View view;
		if (null == convertView) {
			view = inflater.inflate(getResouceID(), parent, false);
			ViewHolder holder = new ViewHolder();
			holder.ivNo = (Button) view.findViewById(R.id.btn_id);
			holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
			holder.tvSummary = (TextView) view.findViewById(R.id.tv_summary);
			view.setTag(holder);
		} else {
			view = convertView;
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.tvTitle.setText(getTitle());
		return view;
	}
	
	protected int getResouceID() {
		return R.layout.fragment_main_list_item;
	}

	class ViewHolder {
		Button ivNo;
		TextView tvTitle;
		TextView tvSummary;
	}
}
