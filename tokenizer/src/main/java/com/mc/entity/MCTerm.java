package com.mc.entity;

public class MCTerm {
	String token;
	int start;
	int end;
	
	public MCTerm(String token, int start, int end) {
		this.token = token;
		this.start = start;
		this.end = end;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
}
