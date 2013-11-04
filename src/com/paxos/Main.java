package com.paxos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paxos.common.PaxosMessage;
import com.paxos.common.PaxosMessageEnum;
import com.paxos.common.Process;

public class Main {
	Map<String, Process> processes=new HashMap<String, Process>();
	public ArrayList<String> leaders = new ArrayList<String>();
	public ArrayList<String> acceptors = new ArrayList<String>();
	public ArrayList<String> clients = new ArrayList<String>();
	public ArrayList<String> replicas = new ArrayList<String>();
	
	public final static int nAcceptors = 3;
	public final static int nReplicas = 2;
	public final static int nLeaders = 2;
	public final static int nRequests = 10;
	public final static int nClients = 2;

	
	synchronized public void sendMessage(String destProcessId, PaxosMessage msg){
		Process p = processes.get(destProcessId);
		if(p!=null) {
			p.deliver(msg);
		}
	}
	
	synchronized public void addProcess(String processId, Process process) {
		if(processes.get(processId)!=null){
			System.err.println("Process Id ("+processId+") already present in the list of processes");
			return;
		}
		processes.put(processId,process);
		process.start();
	}
	
	synchronized public void removeProcess(String processId) {
		processes.remove(processId);
	}
	
	//start off all threads
	void run() {
		
		//acceptor
		for(int i=0;i<nAcceptors;i++) {
			Acceptor a = new Acceptor(this, "ACCEPTOR:"+i);
			addProcess("ACCEPTOR:"+i,a);
			acceptors.add("ACCEPTOR:"+i);
		}		
		System.out.println("spawned acceptors");
		//replica
		for(int i=0;i<nReplicas;i++) {
			Replica r = new Replica(this, "REPLICA:"+i);
			addProcess("REPLICA:"+i,r);
			replicas.add("REPLICA:"+i);
		}
		
		System.out.println("spawned replicas");
		//leader
		for(int i=0;i<nLeaders;i++) {
			Leader l = new Leader(this, "LEADER:"+i, acceptors, replicas);
			addProcess("LEADER:"+i, l);
			leaders.add("LEADER:"+i);
		}
		
		System.out.println("spawned leaders");
		//client
		for(int i=0;i<nClients;i++) {
			Client c = new Client(this, "CLIENT:"+i, replicas);
			addProcess("CLIENT:"+i,c);
			clients.add("CLIENT:"+i);
		}
		
		System.out.println("spawned clients");
		
		//make clients perform requests
		int i=0;
		for(String client : clients) {
			PaxosMessage m = new PaxosMessage();
			m.setMessageType(PaxosMessageEnum.CLIENTINPUT);
			m.setNumClientRequests((i+1)*2);
			sendMessage(client, m);
			i++;
		}
	}
	
	public static void main(String args[]) {
		Main main = new Main();
		main.run();
		
	}
}
