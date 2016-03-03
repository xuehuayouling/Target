package com.ruili.target.entity;

import java.util.List;

public class Category {

	private int id;
	private String name;
	private List<CheckTime> checktime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CheckTime> getChecktime() {
		return checktime;
	}

	public void setChecktime(List<CheckTime> checktime) {
		this.checktime = checktime;
	}

}
