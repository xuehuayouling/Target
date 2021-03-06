package com.ruili.target.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruili.target.R;
import com.ruili.target.utils.JsonUtil;
import com.ruili.target.utils.Logger;

public class Subcategory {
	public static final int INDEX_TYPE_YESNO = 0;
	public static final int INDEX_TYPE_SCORE = 1;
	public static final String STATE_UNDO = "0";
	public static final String STATE_DONE = "1";
	public static final String STATE_CONFIRMED = "2";
	public static final int INDEX_COMPLETE_YES = 1;
	public static final int INDEX_COMPLETE_NO = 0;
	private int index_log_id;
	private int small_index_id;
	private String small_index_name;
	private int index_type;
	private int index_complete = -1;
	private String index_score;
	private String status;
	private String index_remark;
	private List<PicUrl> index_pic;
	private String qc_state;
	private String qc_describe;
	/**
	 * 用于今日指标中的标示是否可以编辑，1：可以编辑：2：不可编辑
	 */
	private String edit_type = "1";

	public String getQc_state() {
		return qc_state;
	}

	public void setQc_state(String qc_state) {
		this.qc_state = qc_state;
	}

	public String getQc_describe() {
		return qc_describe;
	}

	public void setQc_describe(String qc_describe) {
		this.qc_describe = qc_describe;
	}

	public List<PicUrl> getIndex_pic() {
		return index_pic;
	}
	
	public String getIndexPics() {
		List<PicUrl> picUrls = getIndex_pic();
		String picUrlString = null;
		if (picUrls != null && picUrls.size() > 0) {
			for (PicUrl picUrl : picUrls) {
				if (picUrlString == null) {
					picUrlString = picUrl.getPic_url();
				} else {
					picUrlString += ";" + picUrl.getPic_url();
				}
			}
		}
		return picUrlString;
	}

	public void setIndex_pic(List<PicUrl> index_pic) {
		this.index_pic = index_pic;
	}

	public String getIndex_remark() {
		return index_remark;
	}

	public void setIndex_remark(String index_remark) {
		this.index_remark = index_remark;
	}

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

	private String getIndexComplete() {
		if (index_complete == INDEX_COMPLETE_YES) {
			return "1";
		} else if (index_complete == INDEX_COMPLETE_NO) {
			return "0";
		} else {
			return null;
		}
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


	public String getEdit_type() {
		return edit_type;
	}

	public void setEdit_type(String edit_type) {
		this.edit_type = edit_type;
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
	
	public Map<String, String> getUpdataParams(String tag) {
		Logger.debug(tag, "subcategory -->  " + JsonUtil.toJSONString(this));
		Map<String, String> map = new HashMap<String, String>();
		if (index_type == INDEX_TYPE_YESNO && (getIndex_complete() == INDEX_COMPLETE_YES || getIndex_complete() == INDEX_COMPLETE_NO)) {
			map.put("index_complete", getIndexComplete());
		}
		if (index_type == INDEX_TYPE_SCORE) {
			map.put("index_score", String.valueOf(getIndex_score()));
		}
		map.put("index_remark", String.valueOf(getIndex_remark()));
		map.put("index_pic", String.valueOf(getIndexPics()));
		return map;
	}

}
