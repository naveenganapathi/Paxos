package com.paxos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.paxos.common.Ballot;
import com.paxos.common.PValue;
import com.paxos.common.PaxosMessage;
import com.paxos.common.PaxosMessageEnum;
import com.paxos.common.Process;
public class Scout extends Process {

	String leader;
	ArrayList<String> acceptors=new ArrayList<String>();
	Set<PValue> pvalues=new HashSet<PValue>();
	Ballot ballot;

	public Scout(Main main, String myProcessId, String leader, ArrayList<String> acceptors, Ballot ballot) {
		this.main=main;
		this.processId=myProcessId;
		this.leader = leader;
		this.acceptors = acceptors;
		this.ballot = ballot;
		initwriter(myProcessId);
		main.addProcess(processId, this);
	}

	@Override
	public void body() throws Exception {
		writeToLog("scouting now with ballot:D"+ballot);
		PaxosMessage p1amsg=new PaxosMessage();
		p1amsg.setMessageType(PaxosMessageEnum.P1A);
		p1amsg.setSrcId(this.processId);
		p1amsg.setBallot(this.ballot);
		Set<String> waitFor = new HashSet<String>();
		for(String acceptor: acceptors) {
			sendMessage(acceptor, p1amsg);
			waitFor.add(acceptor);
		}
		int acceptorsSize = acceptors.size();
		boolean isPreempt = false;
		while(waitFor.size() >= acceptorsSize/2){
			PaxosMessage msg=messages.dequeue();
			if(msg.getMessageType().equals(PaxosMessageEnum.P1B)){
				if(msg.getBallot().compareWith(this.ballot)!=0){
					writeToLog(this.processId+" seeing a better ballot"+msg.getBallot()+" for"+ballot);
					PaxosMessage preempt=new PaxosMessage();
					preempt.setMessageType(PaxosMessageEnum.PREEMPT);
					preempt.setBallot(msg.getBallot());
					sendMessage(this.leader, preempt);
					isPreempt=true;
					break;
				}
				waitFor.remove(msg.getSrcId());
				pvalues.addAll(msg.getAccepted());
			}			
		}
		if(!isPreempt) {
			PaxosMessage adopted=new PaxosMessage();
			adopted.setMessageType(PaxosMessageEnum.ADOPTED);
			adopted.setAccepted(pvalues);
			adopted.setBallot(ballot);
			sendMessage(this.leader, adopted);
		}
	}

}
