package com.paxos;
import java.util.HashSet;
import java.util.Set;

import com.paxos.common.Ballot;
import com.paxos.common.PValue;
import com.paxos.common.PaxosMessage;
import com.paxos.common.PaxosMessageEnum;
import com.paxos.common.Process;
public class Acceptor extends Process {
	Ballot ballot = null; //initialized to bottom.
	Set<PValue> accepted = new HashSet<PValue>();
	
	public Acceptor(Main env,String procId) {
		this.main = env;
		this.processId = procId;
	}
	
	public void body() {
		System.out.println(this.processId+" is performing the tasks!");
		PaxosMessage pMessage = getNextMessage();
		if(PaxosMessageEnum.P1A.equals(pMessage.getMessageType())) {
			if(ballot == null || ballot.compareWith(pMessage.getBallot()) < 0) {
				ballot = pMessage.getBallot();
			}
			
			//construct p1b message.
			PaxosMessage p1b = new PaxosMessage();
			p1b.setMessageType(PaxosMessageEnum.P1B);
			p1b.setAccepted(accepted);
			p1b.setBallot(ballot);
			p1b.setSrcId(this.processId);
			
			//send the constructed p1b message to the scout.
			sendMessage(pMessage.getSrcId(), p1b);
		} else if (PaxosMessageEnum.P2A.equals(pMessage.getMessageType())) {
			if(ballot == null || ballot.compareWith(pMessage.getBallot()) <= 0) {
				ballot = pMessage.getBallot();
				accepted.add(new PValue(ballot, pMessage.getSlot_number(), pMessage.getRequest()));
			}
			
			//construct p2b message
			PaxosMessage p2b = new PaxosMessage();
			p2b.setMessageType(PaxosMessageEnum.P2B);
			p2b.setBallot(ballot);
			p2b.setSlot_number(pMessage.getSlot_number());
			p2b.setSrcId(this.processId);
			
			//send the constructed p2b message to the commander.
			sendMessage(pMessage.getSrcId(),p2b);
		}
	}

}
