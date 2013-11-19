package com.paxos;
import java.util.HashSet;
import java.util.Set;

import com.paxos.common.Ballot;
import com.paxos.common.PValue;
import com.paxos.common.PaxosMessage;
import com.paxos.common.PaxosMessageEnum;
import com.paxos.common.Process;
import com.paxos.common.Timer;
public class Acceptor extends Process {
	Ballot ballot = null; //initialized to bottom.
	Timer timer;
	Set<PValue> accepted = new HashSet<PValue>();
	
	public Acceptor(Main env,String procId) {
		this.main = env;
		this.processId = procId;
		initwriter(procId);
	}
	
	public void body() throws Exception {
	//	writeToLog(this.processId+" is performing the tasks!");
		while(true && this.alive) {
			PaxosMessage pMessage = getNextMessage();
			if(PaxosMessageEnum.P1A.equals(pMessage.getMessageType())) {
				if((timer == null || timer.hasTimedOut()) && (ballot == null || ballot.compareWith(pMessage.getBallot()) < 0)) {
					ballot = pMessage.getBallot();
					System.out.println(this.processId+" will not accept ballots for "+pMessage.getLeasePeriod()+" seconds.");
					timer = new Timer(pMessage.getLeasePeriod());
					timer.start();
					writeToLog(processId+"adopted ballot:"+ballot);
					//System.out.println("adopted ballot");
				}
				
				//construct p1b message.
				PaxosMessage p1b = new PaxosMessage();
				p1b.setMessageType(PaxosMessageEnum.P1B);
				p1b.setAccepted(accepted);
				p1b.setBallot(ballot);
				p1b.setSrcId(this.processId);
				
				//send the constructed p1b message to the scout.
				sendMessage(pMessage.getSrcId(), p1b);
				//writeToLog(this.processId+"sent p1b message");
			} else if (PaxosMessageEnum.P2A.equals(pMessage.getMessageType())) {
				if(ballot == null || ballot.compareWith(pMessage.getBallot()) <= 0) {
					ballot = pMessage.getBallot();
					writeToLog(processId+"accepted ballot:"+ballot);
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

}
