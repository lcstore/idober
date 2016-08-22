package com.lezo.idober.service;

public enum UserKind {
	USER(""), QQUSER("QQ"), WBUSER("WB");

	private String name = "";

	private UserKind(String name) {
		this.name = name;
	}

	public String value() {
		return this.name;
	}

	@Override
	public String toString() {
		return value();
	}
}
