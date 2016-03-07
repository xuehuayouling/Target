package com.ruili.target.entity;

import java.util.List;

public class CategoryDTO extends ResponseDTO {

	private List<Category> data;

	public List<Category> getData() {
		return data;
	}

	public void setData(List<Category> data) {
		this.data = data;
	}

}
