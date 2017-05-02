package com.whydoito.common.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


import sun.misc.BASE64Decoder;


public class ResultData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8982552729727173047L;

	public static final String SUCC_STATUS = "0";
	public static final String FAIL_STATUS = "1";

	public static final String SUCC_MSG = "success";
	public static final String FAIL_MSG = "fail";

	private Head head = new Head();

	private Result result = new Result();

	private Object data;

	public ResultData() {
	}

	public Head getHead() {
		return head;
	}

	public void setHead(Head head) {
		this.head = head;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data=data;
	}
}
