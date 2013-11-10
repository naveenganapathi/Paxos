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
import com.paxos.common.Timer;

public class Leader extends Process {

	ArrayList<String> acceptors=new ArrayList<String>();
	ArrayList<String> replicas=new ArrayList<String>();
	private static int PING_LEADER_TIME = 2;
	private static int PING_LEADER_REPLY_WAIT_TIME = 2;
	Ballot ballot;
	double timeOut=1;
	private static double INCREASE_FACTOR = 1.5;
	boolean active = false;
	int currentLeader;
	Map<Integer,Request> proposals = new HashMap<Integer,Request>();

	public Leader(Main main, String myProcessId, ArrayList<String> acceptors, ArrayList<String> replicas) {
		this.main=main;
		this.processId=myProcessId;
		this.acceptors = acceptors;
		this.replicas = replicas;
		ballot = new Ballot(myProcessId, 0);
		initwriter(myProcessId);
		currentLeader=0;
	}

	@Override
	public void body() {
		//	writeToLog("spawned"+this.processId);
		new Scout(main, "SCOUT:"+ballot.getString(), processId, acceptors, ballot);
		while(true & this.alive){
			//System.out.println("ProcessId and current leader:"+this.processId+","+currentLeader);
			if(("LEADER:"+currentLeader).equalsIgnoreCase(this.processId) ) {
				PaxosMessage msg=messages.dequeue();
				//System.out.println("here buddy - "+msg);
				if(!this.alive)
					break;
				if(msg.getMessageType().equals(PaxosMessageEnum.PROPOSE)) {
					//writeToLog(this.processId+" acquried proposal "+msg.getRequest());
					if(!proposals.containsKey(msg.getSlot_number())) {
						//writeToLog(this.processId+": Adding to proposals - "+proposals);
						proposals.put(msg.getSlot_number(), msg.getRequest());
						if(active) {
							new Commander(main, "COMMANDER:"+ballot.getString()+","+msg.getSlot_number(),processId, acceptors, replicas, ballot, msg.getSlot_number(), msg.getRequest());
						} else {
							writeToLog(this.processId+"is not active! hence not proposing now.");
						}
					}
				}
				if(msg.getMessageType().equals(PaxosMessageEnum.ADOPTED)) {
					if(ballot.equals(msg.getBallot())) {
						//		writeToLog(this.processId+"working! msg:"+msg);
						Map<Integer,Ballot> pmax = new HashMap<Integer,Ballot>();
						for(PValue pvalue:msg.getAccepted()) {
							Ballot bal = pmax.get(pvalue.getSlot_number());
							if(bal==null || bal.compareWith(pvalue.getBallot())<0) {
								pmax.put(pvalue.getSlot_number(), pvalue.getBallot());
								if(pvalue.getRequest() == null)
									writeToLog("null pvalue!!!");
								proposals.put(pvalue.getSlot_number(), pvalue.getRequest());
							}
						}
						//	writeToLog(this.processId+"proposals:"+proposals);
						for(int i:proposals.keySet()) {
							writeToLog(this.processId+"creating commander for proposals:"+proposals.get(i).getClientId()+","+proposals.get(i).getClientCommandId());
							new Commander(main, "COMMANDER:"+ballot.getString()+","+i,processId,acceptors,replicas,ballot,i,proposals.get(i));
						}
						/*timeOut-=1;

						//time out can never go below 1.
						if(timeOut < 1) {
							System.err.println("Time out has become negative.");
							timeOut = 1;
						}*/
						active = true;
					}
				}
				if(msg.getMessageType().equals(PaxosMessageEnum.PREEMPT)) {
					//	writeToLog(this.processId+" ballot pre-empted!");
					//	writeToLog("comparing "+ballot+" with "+msg.getBallot()+", res:"+ballot.compareWith(msg.getBallot()));
					if(ballot.compareWith(msg.getBallot()) < 0) {
						/*Double delay = timeOut*INCREASE_FACTOR*20;
						writeToLog(this.processId+": Delaying the spawn of a new scout by "+delay.intValue()+" seconds");
						try {
							Thread.sleep(delay.intValue()*1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						ballot = new Ballot(processId, msg.getBallot().getBallotId()+1);
						writeToLog(this.processId+" trying with new ballot"+ballot);
						new Scout(main, "SCOUT:"+ballot.getString(), processId, acceptors, ballot);
						active = false;
					}
				}
				if(msg.getMessageType().equals(PaxosMessageEnum.LEADERCHECK)) {
					writeToLog(this.processId+": Ping from "+msg.getSrcId()+" received");
					PaxosMessage message = new PaxosMessage();
					message.setMessageType(PaxosMessageEnum.LEADERCHECKACK);
					try {
						sendMessage(msg.getSrcId(), message);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				active = true;
				try {
					Thread.sleep(PING_LEADER_TIME*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				PaxosMessage message = new PaxosMessage();
				message.setMessageType(PaxosMessageEnum.LEADERCHECK);
				try {
					sendMessage("LEADER:"+currentLeader, message);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					Thread.sleep(PING_LEADER_REPLY_WAIT_TIME*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				boolean found=false;
				PaxosMessage toDelete = null;
				for(PaxosMessage msg:messages.list) {
					if(msg.getMessageType().equals(PaxosMessageEnum.LEADERCHECKACK)) {
						found=true;
						toDelete = msg;
						break;						
					}
				}
				
				if(!found) {
					writeToLog(this.processId+": Current Leader Dead. Changing current leader to "+(int)(currentLeader+1));
					currentLeader++;
				} else {
					writeToLog(this.processId+": Ack from "+toDelete.getSrcId()+" received");
					messages.list.remove(toDelete);
				}
			}
		}

		if(!this.alive) {
			System.err.println("SUCCESSFULLY KILLED THE LEADER MUWHAAHAHA - "+this.processId);
		}
	}
}