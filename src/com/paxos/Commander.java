package com.paxos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.paxos.common.Ballot;
import com.paxos.common.PaxosMessage;
import com.paxos.common.PaxosMessageEnum;
import com.paxos.common.Process;
import com.paxos.common.Request;

public class Commander extends Process{

	String leader;
	ArrayList<String> acceptors=new ArrayList<String>();
	ArrayList<String> replicas=new ArrayList<String>();
	Ballot ballot;
	int slot_number;
	Request request;

	public Commander(Main main, String myProcessId, String leader, ArrayList<String> acceptors, ArrayList<String> replicas, Ballot ballot,
			int slot_number, Request request) {
		this.main=main;
		this.processId=myProcessId;
		this.leader = leader;
		this.acceptors = acceptors;
		this.replicas = replicas;
		this.ballot = ballot;
		this.slot_number = slot_number;
		this.request = request;
		initwriter(myProcessId);
		writeToLog(this.processId+ " created commander for"+request);
		main.addProcess(this.processId, this);
	}

	@Override
	public void body() throws Exception {
		PaxosMessage p2amsg=new PaxosMessage();
		p2amsg.setMessageType(PaxosMessageEnum.P2A);
		p2amsg.setSrcId(this.processId);
		p2amsg.setBallot(this.ballot);
		p2amsg.setRequest(request);
		Set<String> waitFor = new HashSet<String>();
		for(String acceptor: acceptors) {
			sendMessage(acceptor, p2amsg);
			waitFor.add(acceptor);
		}
		int acceptorsSize = acceptors.size();
		boolean isPreempt = false;
		while(waitFor.size() >= acceptorsSize/2){
			PaxosMessage msg=messages.dequeue();
			if(msg.getMessageType().equals(PaxosMessageEnum.P2B)){
				if(msg.getBallot().compareWith(this.ballot)!=0){
					PaxosMessage preempt=new PaxosMessage();
					preempt.setMessageType(PaxosMessageEnum.PREEMPT);
					preempt.setBallot(msg.getBallot());
					writeToLog(this.processId+" PREEMPTED!! for ballot:"+ballot);
					sendMessage(this.leader, preempt);
					isPreempt=true;
					break;
				}
				waitFor.remove(msg.getSrcId());
			}			
		}
		if(!isPreempt) {
			PaxosMessage decision=new PaxosMessage();
			decision.setSlot_number(slot_number);
			decision.setRequest(request);
			decision.setMessageType(PaxosMessageEnum.PERFORM);
			writeToLog(this.processId+" NOT PREEMPTED!!"+decision.getRequest());
			//writeToLog(decision);
			for(String replica: replicas) {
				sendMessage(replica, decision);
			}
		}
	}
}
