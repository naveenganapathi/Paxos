package com.paxos;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.paxos.common.PaxosMessage;
import com.paxos.common.PaxosMessageEnum;
import com.paxos.common.Process;
import com.paxos.common.Request;
public class Replica extends Process{

	int slotNumber;
	int minUndecided;	
	Map<Integer,Request> proposals = new HashMap<Integer,Request>();
	Map<Integer,Request> decisions = new HashMap<Integer,Request>();
	public Replica(Main env, String procId) {
		this.main = env;
		this.processId = procId;
	}
	
	
	public void propose(Request r) {
		while(decisions.containsKey(minUndecided)) {
			minUndecided++;
		}
		PaxosMessage proposal = new PaxosMessage();
		proposal.setRequest(r);
		proposal.setMessageType(PaxosMessageEnum.PROPOSE);
		proposal.setSlot_number(minUndecided);
		proposal.setSrcId(this.processId);
		List<String> leaders = this.main.leaders;
		for(String leader : leaders) {
			//System.out.println(this.processId+" proposing to leader!");
			main.sendMessage(leader, proposal);	
		}						
	}
	
	public void perform(Request r) {		
		//check if already performed.
		for(Entry<Integer,Request> entry : decisions.entrySet()) {
			System.out.println("inside perform"+entry);
			if(entry.getKey() < slotNumber && entry.getValue().equals(r)) {
			
				System.out.println("request already performed returning for slot"+entry.getKey()+", which is before"+slotNumber);
				return;
			}
		}
		System.out.println(this.processId+"performing "+r);
		slotNumber++;
	}
	
	public void body() {
		System.out.println(this.processId+"running now");
		slotNumber = 1;
		minUndecided = 1;
		while(true) {
			PaxosMessage pMessage = getNextMessage();
			if(PaxosMessageEnum.PERFORM.equals(pMessage.getMessageType())) {
				if(pMessage.getRequest() == null)
				System.out.println("message with null request!"+pMessage);
				decisions.put(pMessage.getSlot_number(), pMessage.getRequest());
				while(true) {
					Request r = decisions.get(slotNumber);
					if(r==null)
						break;
					
					//propose again in case the request for the slot is different from the request you proposed.
					if(proposals.containsKey(slotNumber) 
							&& !decisions.get(slotNumber).equals(proposals.get(slotNumber))) {
						propose(proposals.get(slotNumber));
					}
					
					//perform the current task;
					perform(r);					
				}
			} else if (PaxosMessageEnum.REQUEST.equals(pMessage.getMessageType())) {
			//	System.out.println("proposing for the request !");
			  propose(pMessage.getRequest());	
			}			
			else {
				System.out.println("UNKNOWN message type "+pMessage.getMessageType());
			}
		}
	}
}
