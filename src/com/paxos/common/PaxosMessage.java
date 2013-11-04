package com.paxos.common;

import java.util.Set;

public class PaxosMessage {
	PaxosMessageEnum messageType;
	String srcId;
	Ballot ballot;
	Set<PValue> accepted;
	int slot_number;
	Integer numClientRequests;
	Request request;
	public PaxosMessageEnum getMessageType() {
		return messageType;
	}
	public void setMessageType(PaxosMessageEnum messageType) {
		this.messageType = messageType;
	}
	public String getSrcId() {
		return srcId;
	}
	public void setSrcId(String srcId) {
		this.srcId = srcId;
	}
	public Ballot getBallot() {
		return ballot;
	}
	public void setBallot(Ballot ballot) {
		this.ballot = ballot;
	}
	public Set<PValue> getAccepted() {
		return accepted;
	}
	public void setAccepted(Set<PValue> accepted) {
		this.accepted = accepted;
	}
	public int getSlot_number() {
		return slot_number;
	}
	public void setSlot_number(int slot_number) {
		this.slot_number = slot_number;
	}
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
		
	public Integer getNumClientRequests() {
		return numClientRequests;
	}
	public void setNumClientRequests(Integer numClientRequests) {
		this.numClientRequests = numClientRequests;
	}
	@Override
	public String toString() {
		return "PaxosMessage [messageType=" + messageType + ", srcId=" + srcId
				+ ", ballot=" + ballot + ", accepted=" + accepted
				+ ", slot_number=" + slot_number + ", numClientRequests="
				+ numClientRequests + ", request=" + request + "]";
	}
	
}
