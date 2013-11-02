package com.paxos;

import java.util.ArrayList;

import com.paxos.common.Ballot;
import com.paxos.common.Process;
import com.paxos.common.Request;

public class Commander extends Process{

	String leader;
	ArrayList<String> acceptors=new ArrayList<String>();
	Ballot ballot;
	int slot_number;
	Request request;
	
	public Commander(Main main, String myProcessId, String leader, ArrayList<String> acceptors, Ballot ballot,
			int slot_number, Request request) {
		this.main=main;
		this.processId=myProcessId;
		this.leader = leader;
		this.acceptors = acceptors;
		this.ballot = ballot;
		this.slot_number = slot_number;
		this.request = request;
	}
	
	@Override
	public void body() {
		
		
	}
	
}
