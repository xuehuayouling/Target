package com.ruili.target.entity;

import java.util.List;

public class SubcategoryDTO extends ResponseDTO {

	private List<Subcategory> data;

	public List<Subcategory> getData() {
		return data;
	}

	public void setData(List<Subcategory> data) {
		this.data = data;
	}
	
}
