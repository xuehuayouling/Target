package com.ruili.target.entity;

public class CheckTime {

	public static final int CHECK_TIME_NULL = -1;
	private int id;
	private String starttime;
	private String endtime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	@Override
	public String toString() {
		return starttime + "-" + endtime;
	}

}
