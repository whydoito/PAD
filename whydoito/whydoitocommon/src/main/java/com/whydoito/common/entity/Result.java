package com.whydoito.common.entity;

import java.io.Serializable;


public class Result implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3836763576823673157L;

	private String code = "";
	
	private String dateTime = "";
	
	private String message = "";

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
	
	
}
