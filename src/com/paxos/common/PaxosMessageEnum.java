package com.paxos.common;

public enum PaxosMessageEnum {
	P1A("p1a"),
	P1B("p1b"),
	P2A("p2a"),
	P2B("p2b"),
	PREEMPT("preempt"),
	ADOPTED("adopted"),
	PROPOSE("propose"),
	PERFORM("perform"),
	DECISION("decision");
	String messageLabel;
	PaxosMessageEnum(String message) {
		this.messageLabel=message;
	}
	public String getMessageLabel() {
		return messageLabel;
	}
	public void setMessageLabel(String messageLabel) {
		this.messageLabel = messageLabel;
	}
}
