package com.paxos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.paxos.common.Ballot;
import com.paxos.common.PValue;
import com.paxos.common.PaxosMessage;
import com.paxos.common.PaxosMessageEnum;
import com.paxos.common.Process;
import com.paxos.common.Request;

public class Leader extends Process {

	ArrayList<String> acceptors=new ArrayList<String>();
	ArrayList<String> replicas=new ArrayList<String>();
	Ballot ballot;
	boolean active = false;
	Map<Integer,Request> proposals = new HashMap<Integer,Request>();

	public Leader(Main main, String myProcessId, ArrayList<String> acceptors, ArrayList<String> replicas) {
		this.main=main;
		this.processId=myProcessId;
		this.acceptors = acceptors;
		this.replicas = replicas;
		ballot = new Ballot(myProcessId, 0);
	}

	@Override
	public void body() {
		System.out.println("spawned"+this.processId);
		new Scout(main, "S:"+ballot.getString(), processId, acceptors, ballot);
		
		while(true){
			PaxosMessage msg=messages.dequeue();
			if(msg.getMessageType().equals(PaxosMessageEnum.PROPOSE)) {
			//	System.out.println(this.processId+"acquried proposal!"+msg+", proposals"+proposals);
				if(!proposals.containsKey(msg.getSlot_number())) {
					proposals.put(msg.getSlot_number(), msg.getRequest());
					if(active) {
						new Commander(main, "C:"+ballot.getString()+":"+msg.getSlot_number(),processId, acceptors, replicas, ballot, msg.getSlot_number(), msg.getRequest());
					} else {
						System.out.println(this.processId+"is not active! hence not proposing now.");
					}
				}
			}
			if(msg.getMessageType().equals(PaxosMessageEnum.ADOPTED)) {
				if(ballot.equals(msg.getBallot())) {
					System.out.println(this.processId+"working! msg:"+msg);
					Map<Integer,Ballot> pmax = new HashMap<Integer,Ballot>();
					for(PValue pvalue:msg.getAccepted()) {
						Ballot bal = pmax.get(pvalue.getSlot_number());
						if(bal==null || bal.compareWith(pvalue.getBallot())<0) {
							pmax.put(pvalue.getSlot_number(), pvalue.getBallot());
							if(pvalue.getRequest() == null)
								System.err.println("null pvalue!!!");
							proposals.put(pvalue.getSlot_number(), pvalue.getRequest());
						}
					}
				//	System.out.println(this.processId+"proposals:"+proposals);
					for(int i:proposals.keySet()) {
						System.out.println(this.processId+"creating commander for proposals"+proposals.get(i));
						new Commander(main, "C:"+ballot.getString()+":"+msg.getSlot_number(),processId,acceptors,replicas,ballot,i,proposals.get(i));
					}
					active = true;
				}
			}
			if(msg.getMessageType().equals(PaxosMessageEnum.PREEMPT)) {
				System.out.println(this.processId+" ballot pre-empted!");
				System.out.println("comparing "+ballot+" with "+msg.getBallot()+", res:"+ballot.compareWith(msg.getBallot()));
				if(ballot.compareWith(msg.getBallot()) < 0) {
					ballot = new Ballot(processId, msg.getBallot().getBallotId()+1);
					System.out.println(this.processId+"trying with new ballot"+ballot);
					new Scout(main, "S:"+ballot.getString(), processId, acceptors, ballot);
					active = false;
				}
			}
		}
	}
}
