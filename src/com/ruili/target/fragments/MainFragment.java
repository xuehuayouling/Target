package com.ruili.target.fragments;

import java.util.ArrayList;
import java.util.List;

import com.ruili.target.R;
import com.ruili.target.adapters.MainFragmentAdapter;
import com.ruili.target.entity.Entity1;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainFragment extends ListFragment {
	String[] cities = {
	         "Shenzhen",
	         "Beijing",
	         "Shanghai",
	         "Guangzhou",
	         "Wuhan",
	         "Tianjing",
	         "Changsha",
	         "Xi'an",
	         "Chongqing",
	         "Guilin",
	    };
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<Entity1> list = new ArrayList<>();
		for (String title : cities) {
			Entity1 entity = new Entity1();
			entity.setTitle(title);
			list.add(entity);
		}
		this.setListAdapter(new MainFragmentAdapter(getActivity(), list));
	}

}
