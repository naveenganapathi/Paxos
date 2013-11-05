package com.paxos;
import java.util.List;

import com.paxos.common.BankCommand;
import com.paxos.common.CommandEnum;
import com.paxos.common.PaxosMessage;
import com.paxos.common.PaxosMessageEnum;
import com.paxos.common.Process;
import com.paxos.common.Request;
public class Client extends Process {

	List<String> replicas;
	int clientCommandId;
	
	public Client (Main main,String pId,List<String> replicas) {
		this.processId = pId;
		this.main = main;
		this.replicas = replicas;
		
	}
	@Override
	public void body() {
	//	System.out.println("spawned"+this.processId);
		while(true) {			
			PaxosMessage pMessage = getNextMessage();
		//	System.out.println("message:"+pMessage);
			if(PaxosMessageEnum.CLIENTINPUT.equals(pMessage.getMessageType())) {
				
				//generate a number of client requests
				if(pMessage.getNumClientRequests() != null) {
					
					for(int i=0;i<pMessage.getNumClientRequests();i++) {
						try {
							Thread.sleep(2000l);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Request r = new Request(this.processId, clientCommandId++, "INSERT"+i,new BankCommand(CommandEnum.DEPOSIT, "A1", null, (float)((i+1)*10.00)));
						PaxosMessage m = new PaxosMessage();
						m.setRequest(r);
						m.setSrcId(processId);
						m.setMessageType(PaxosMessageEnum.REQUEST);
						for(String replica : replicas) {
							//System.out.println("sending requests to replicas"+m);
							main.sendMessage(replica, m);
						}
					}
					
				}
				
				//place the given request
				PaxosMessage m = new PaxosMessage();
				m.setRequest(pMessage.getRequest());
				m.setSrcId(processId);
				m.setMessageType(PaxosMessageEnum.REQUEST);
				for(String replica : replicas) {							
					main.sendMessage(replica, m);
				}
				
			} 
			 
		}
	}

}
