package com.scb.gmr;

public interface EventBus {
	public void fire(String type, String reason);
}
