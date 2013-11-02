package com.paxos.common;

public class PValue {
	Ballot ballot;
	int slot_number;
	Request request;
	public PValue(Ballot ballot, int slot_number, Request request){
		this.ballot=ballot;
		this.slot_number=slot_number;
		this.request=request;
	}
	public Ballot getBallot() {
		return ballot;
	}
	public void setBallot(Ballot ballot) {
		this.ballot = ballot;
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
	@Override
	public String toString() {
		return "PValue [ballot=" + ballot + ", slot_number=" + slot_number
				+ ", request=" + request + "]";
	}	
}
