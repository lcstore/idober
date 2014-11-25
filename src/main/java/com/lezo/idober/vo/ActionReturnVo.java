package com.lezo.idober.vo;

import java.io.Serializable;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年11月25日
 */
public class ActionReturnVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private int code;
	private String msg;
	private Serializable data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Serializable getData() {
		return data;
	}

	public void setData(Serializable data) {
		this.data = data;
	}
}
