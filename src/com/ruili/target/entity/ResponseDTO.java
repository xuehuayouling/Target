package com.ruili.target.entity;

public abstract class ResponseDTO {

	protected int error_code = 1;
	
	public int getError_code() {
		return error_code;
	}
	
	public void setError_code(int error_code) {
		this.error_code = error_code;
	}

	public boolean isValid() {
		return 0 == error_code;
	}

	abstract public Object getData();
	
}
