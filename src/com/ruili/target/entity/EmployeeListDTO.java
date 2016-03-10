package com.ruili.target.entity;

import java.util.List;

public class EmployeeListDTO extends ResponseDTO {

	private List<Employee> data;

	public List<Employee> getData() {
		return data;
	}

	public void setData(List<Employee> data) {
		this.data = data;
	}
	
}
