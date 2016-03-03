package com.ruili.target.entity;

public class User {

	public static final String SHAREDPREFERENCES_KEY = "current_user";
	public static final String SHAREDPREFERENCES_NAME = "name";
	public static final String SHAREDPREFERENCES_TYEP = "type";
	public static final String SHAREDPREFERENCES_OPERATOR_ID = "operator_id";
	public static final int TYPE_SUPERVISOR = 0;
	public static final int TYPE_MANAGER = 1;
	public static final int TYPE_QC = 2;

	private int operator_id;
	private String name;
	private int type;

	public int getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(int operator_id) {
		this.operator_id = operator_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
