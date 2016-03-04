package com.ruili.target.entity;

import com.ruili.target.R;

public class Subcategory {
	public static final int INDEX_TYPE_YESNO = 0;
	public static final int INDEX_TYPE_SCORE = 1;
	public static final String STATE_UNDO = "0";
	public static final String STATE_DONE = "1";
	public static final String STATE_CONFIRMED = "2";
	private int index_log_id;
	private int small_index_id;
	private String small_index_name;
	private int index_type;
	private int index_complete;
	private String index_score;
	private String status;

	public int getIndex_log_id() {
		return index_log_id;
	}

	public void setIndex_log_id(int index_log_id) {
		this.index_log_id = index_log_id;
	}

	public int getSmall_index_id() {
		return small_index_id;
	}

	public void setSmall_index_id(int small_index_id) {
		this.small_index_id = small_index_id;
	}

	public String getSmall_index_name() {
		return small_index_name;
	}

	public void setSmall_index_name(String small_index_name) {
		this.small_index_name = small_index_name;
	}

	public int getIndex_type() {
		return index_type;
	}

	public void setIndex_type(int index_type) {
		this.index_type = index_type;
	}

	public int getIndex_complete() {
		return index_complete;
	}

	public void setIndex_complete(int index_complete) {
		this.index_complete = index_complete;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIndex_score() {
		return index_score;
	}

	public void setIndex_score(String index_score) {
		this.index_score = index_score;
	}

	public int getStateResourceID() {
		int id = -1;
		switch (status) {
		case STATE_UNDO:
			id = R.color.subcategory_state_undo;
			break;
		case STATE_DONE:
			id = R.color.subcategory_state_done;
			break;
		case STATE_CONFIRMED:
			id = R.color.subcategory_state_confirmed;
			break;
		default:
			break;
		}
		return id;
	}

}
