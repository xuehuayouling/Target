package com.ruili.target.entity;

public class ResponseDTO {

	private int error_code;
	private String data;
	
	public boolean isValid() {
		return 0 == error_code;
	}
	
	public String getData() {
		return data;
	}
	
}
