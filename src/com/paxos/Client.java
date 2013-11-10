package com.paxos;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		super();
		this.processId = pId;
		this.main = main;
		this.replicas = replicas;
		initwriter(pId);

	}
	@Override
	public void body() throws Exception {
		//	writeToLog("spawned"+this.processId);
		while(true & this.alive) {		
			PaxosMessage pMessage = getNextMessage();
			//	writeToLog("message:"+pMessage);
			if(PaxosMessageEnum.CLIENTINPUT.equals(pMessage.getMessageType())) {

				//generate a number of client requests
				if(pMessage.getNumClientRequests() != null) {

					for(int i=0;i<pMessage.getNumClientRequests();i++) {
						writeToLog(this.processId+": Sending request for "+i);
//						try {
//							Thread.sleep(2000l);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						Request r = new Request(this.processId, clientCommandId++, "INSERT"+i,new BankCommand(CommandEnum.DEPOSIT, "A1", null, (float)(10.00)));
						PaxosMessage m = new PaxosMessage();
						m.setRequest(r);
						m.setSrcId(processId);
						m.setMessageType(PaxosMessageEnum.REQUEST);
						for(String replica : replicas) {
							//writeToLog("sending requests to replicas"+m);
							sendMessage(replica, m);
						}
						
						while(true) {
							PaxosMessage msg = getNextMessage();
							if(msg.getRequest().getClientId().equals(processId) && msg.getRequest().getClientCommandId() == i) {
								break;
							}
						}
					}

				} else {
					BufferedReader br;
					try {
						br = new BufferedReader(new FileReader(this.processId.replaceAll(":", "_")+"INP"+".txt"));						
						int i=0;
						String temp = null;
						while((temp = br.readLine()) != null) {
							writeToLog(this.processId+": Sending request for "+i);
							Thread.sleep(2000l);
							String[] vals = temp.split(",");							
							Request r = new Request(this.processId, clientCommandId++, "INSERT"+i,new BankCommand(CommandEnum.valueOf(vals[0]), vals[1], vals[2], Float.parseFloat(vals[3])));
							PaxosMessage m = new PaxosMessage();
							m.setRequest(r);
							m.setSrcId(processId);
							m.setMessageType(PaxosMessageEnum.REQUEST);
							for(String replica : replicas) {
								//writeToLog("sending requests to replicas"+m);
								sendMessage(replica, m);
							}
							
							while(true) {
								PaxosMessage msg = getNextMessage();
								if(msg.getRequest().getClientId().equals(processId) && msg.getRequest().getClientCommandId() == i) {
									writeToLog(this.processId+" Got response for request:"+msg.getRequest().getClientCommandId());
									break;
								}
							}
							i++;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				//				//place the given request
				//				PaxosMessage m = new PaxosMessage();
				//				m.setRequest(pMessage.getRequest());
				//				m.setSrcId(processId);
				//				m.setMessageType(PaxosMessageEnum.REQUEST);
				//				for(String replica : replicas) {							
				//					main.sendMessage(replica, m);
				//				}

			} 

		}
	}

}
