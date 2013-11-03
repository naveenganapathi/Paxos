package com.paxos;
import java.util.HashMap;
import java.util.Map;

import com.paxos.common.PaxosMessage;
import com.paxos.common.PaxosMessageEnum;
import com.paxos.common.Process;
import com.paxos.common.Request;
public class Replica extends Process{

	int slotNumber;
	Map<Integer,Request> proposals = new HashMap<Integer,Request>();
	
	public Replica(Main env, String procId) {
		this.main = env;
		this.processId = procId;
	}
	
	public void perform(Request r) {
		System.out.println(this.processId+"performing "+r);
	}
	
	public void body() {
		System.out.println(this.processId+"running now");
		while(true) {
			PaxosMessage pMessage = getNextMessage();
			if(PaxosMessageEnum.PERFORM.equals(pMessage.getMessageType())) {
				proposals.put(pMessage.getSlot_number(), pMessage.getRequest());
				while(true) {
					Request r = proposals.get(pMessage.getSlot_number());
					if(r==null)
						break;
					perform(r);
					slotNumber++;
				}
			} else {
				System.out.println("UNKNOWN message type "+pMessage.getMessageType().getMessageLabel());
			}
		}
	}
}
