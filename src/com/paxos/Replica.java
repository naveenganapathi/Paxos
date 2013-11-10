package com.paxos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.paxos.common.BankAccount;
import com.paxos.common.BankClient;
import com.paxos.common.BankCommand;
import com.paxos.common.PaxosMessage;
import com.paxos.common.PaxosMessageEnum;
import com.paxos.common.Process;
import com.paxos.common.Request;
public class Replica extends Process{

	int slotNumber;
	int minUndecided;	
	Map<Integer,Request> proposals = new HashMap<Integer,Request>();
	Map<Integer,Request> decisions = new HashMap<Integer,Request>();
	List<BankClient> clients;
	Map<String,BankAccount> accntMap;
	public Replica(Main env, String procId) {
		this.main = env;
		this.processId = procId;
		initwriter(procId);
		BankAccount accntA1 = new BankAccount("A1",(float)0.0);
		BankAccount accntA2 = new BankAccount("A2",(float)0.0);
		List<BankAccount> c1Accnts = new ArrayList<BankAccount>();c1Accnts.add(accntA1);
		List<BankAccount> c2Accnts = new ArrayList<BankAccount>();c2Accnts.add(accntA2);
		accntMap = new HashMap<String,BankAccount>();
		accntMap.put("A1", accntA1);
		accntMap.put("A2", accntA2);
		clients = new ArrayList<BankClient>();
		clients.add(new BankClient("C1", c1Accnts));
		clients.add(new BankClient("C2", c2Accnts));
	}
	
	
	public void propose(Request r) throws Exception {
		if(!decisions.containsValue(r)) {
			minUndecided = 1;
			while(decisions.containsKey(minUndecided) || proposals.containsKey(minUndecided)) {
				minUndecided++;
			}
			proposals.put(minUndecided, r);
			PaxosMessage proposal = new PaxosMessage();
			proposal.setRequest(r);
			proposal.setMessageType(PaxosMessageEnum.PROPOSE);
			proposal.setSlot_number(minUndecided);
			proposal.setSrcId(this.processId);
			List<String> leaders = this.main.leaders;
			writeToLog(this.processId+": Proposing slot "+minUndecided+" for request "+r);
			for(String leader : leaders) {
				//writeToLog(this.processId+" proposing to leader!");
				sendMessage(leader, proposal);	
			}	
		}						
	}
	
	public void perform(Request r) {		
		//check if already performed.
		for(Entry<Integer,Request> entry : decisions.entrySet()) {
			//writeToLog("inside perform"+entry);
			if(entry.getKey() < slotNumber && entry.getValue().equals(r)) {
			
				writeToLog("request already performed returning for slot"+entry.getKey()+", which is before"+slotNumber);
				slotNumber++;
				return;
			}
		}
		//writeToLog(this.processId+"performing "+r);
		//writeToLog(this.processId+" map before perfoming the change "+r+":\n"+accntMap);
		BankCommand bc = r.getbCommand();
		BankAccount src = accntMap.get(bc.getSrcAccntId());
	    BankAccount dest = accntMap.get(bc.getDestAccntId());
		switch(bc.getCommandEnum()) {
		case DEPOSIT: src.setBalance(src.getBalance()+bc.getAmt()); break;
		case WITHDRAW: src.setBalance(src.getBalance() - bc.getAmt()); break;
		case TRANSFER: src.setBalance(src.getBalance() - bc.getAmt()); dest.setBalance(dest.getBalance()+bc.getAmt()); break;
		}
		writeToLog(this.processId+" map after perfoming at slot "+slotNumber+" the change "+r+":\n"+accntMap);
		slotNumber++;
		
		//SEND RESPONSE to CLIENT.
	}
	
	public void body() throws Exception {
		//writeToLog(this.processId+"running now");
		slotNumber = 1;
		minUndecided = 1;
		while(true && this.alive) {
			PaxosMessage pMessage = getNextMessage();
			if(PaxosMessageEnum.PERFORM.equals(pMessage.getMessageType())) {
				writeToLog(this.processId+": Received decision for slot : "+pMessage.getSlot_number());
				if(pMessage.getRequest() == null)
				writeToLog("message with null request!"+pMessage);
				decisions.put(pMessage.getSlot_number(), pMessage.getRequest());
				while(decisions.containsKey(slotNumber)) {
					Request r = decisions.get(slotNumber);
					//propose again in case the request for the slot is different from the request you proposed.
					if(proposals.containsKey(slotNumber) 
							&& !decisions.get(slotNumber).equals(proposals.get(slotNumber))) {
						propose(proposals.get(slotNumber));
					}
					
					//perform the current task;
					perform(r);					
				}
			} else if (PaxosMessageEnum.REQUEST.equals(pMessage.getMessageType())) {
				writeToLog(this.processId+" proposing for the request "+pMessage.getRequest());
			  propose(pMessage.getRequest());	
			}			
			else {
				writeToLog("UNKNOWN message type "+pMessage.getMessageType());
			}
		}
	}
}
