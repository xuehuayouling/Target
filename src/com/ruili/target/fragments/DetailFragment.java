package com.ruili.target.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ruili.target.R;
import com.ruili.target.adapters.DetailsFragmentAdapter;
import com.ruili.target.adapters.MainFragmentAdapter;
import com.ruili.target.entity.Entity1;
import com.ruili.target.entity.Entity2;
import com.ruili.target.entity.Entity3;
import com.ruili.target.entity.Entity4;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class DetailFragment extends ListFragment {
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
		return inflater.inflate(R.layout.fragment_details, container, false);
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
			int random = new Random().nextInt(3);
			switch (random) {
			case 0:
				entity = new Entity2();
				break;
			case 1:
				entity = new Entity3();
				break;
			case 2:
				entity = new Entity4();
				break;
			default:
				break;
			}
			
			entity.setTitle(title);
			list.add(entity);
		}
		this.setListAdapter(new DetailsFragmentAdapter(getActivity(), list));
	}

}
