package com.paxos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paxos.common.PaxosMessage;
import com.paxos.common.PaxosMessageEnum;
import com.paxos.common.Process;

public class Main {
	public Map<String, Process> processes=new HashMap<String, Process>();
	public ArrayList<String> leaders = new ArrayList<String>();
	public ArrayList<String> acceptors = new ArrayList<String>();
	public ArrayList<String> clients = new ArrayList<String>();
	public ArrayList<String> replicas = new ArrayList<String>();
	public Map<String,Map<String,Integer>> faultMap = new HashMap<String,Map<String,Integer>>();
	
	public final static int nAcceptors = 3;
	public final static int nReplicas = 2;
	public final static int nLeaders = 2;
	public final static int nRequests = 10;
	public final static int nClients = 2;

	public void initFaultMap(String val[]) {
		int size = val.length;
		for(int i=0;i<size;i++) {
			String temp[] = val[i].split("#");
			if(faultMap.get(temp[0]) == null) {
				Map<String,Integer> processFaultMap = new HashMap<String,Integer>();
				faultMap.put(temp[0], processFaultMap);
			}
			int v = Integer.parseInt(temp[2]);
			if(val[i].contains("RECEIVE")) {
				v++;
			}
			faultMap.get(temp[0]).put(temp[1], v);
			System.out.println("FAULT MAP:"+faultMap);
		}
	}
	
	public String getLeader(String pId) {
		if (pId.contains("SCOUT") || pId.contains("COMMANDER")) {
			String temp[] = pId.split(":"); String l[] = temp[2].split(",");
			String leader = "LEADER:"+l[0];
			return leader;
		} else {
			return null;
		}
	}
	public boolean hasBreached(String pId, String key) {
		if(faultMap.containsKey(pId) && faultMap.get(pId).containsKey(key)) {
			return (faultMap.get(pId).get(key) <= 0 );	
		} else {
			if (pId.contains("SCOUT") || pId.contains("COMMANDER")) {
				String leader = getLeader(pId);
				System.out.println("LEADER:"+leader);
				if(faultMap.containsKey(leader) && faultMap.get(leader).containsKey(key)) {
					if (faultMap.get(leader).get(key) <= 0 && processes.get(leader) != null) {
						processes.get(leader).alive = false;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void updateFMap(String pId,String key) {
		if(faultMap.containsKey(pId) && faultMap.get(pId).containsKey(key)) {
			faultMap.get(pId).put(key, faultMap.get(pId).get(key) - 1);	
		} else {
			if (pId.contains("SCOUT") || pId.contains("COMMANDER")) {
				String leader = getLeader(pId);
				if(faultMap.containsKey(leader) && faultMap.get(leader).containsKey(key)) {
					faultMap.get(leader).put(key, faultMap.get(leader).get(key) - 1);	
				}
				
			}
		}
	}
	
	synchronized public void sendMessage(String srcProcessId,String destProcessId, PaxosMessage msg) throws Exception{
		if(hasBreached(srcProcessId,"SEND,"+msg.getMessageType())) {
			throw new Exception("Send count breached before a send can be performed. inducing an exception.");
		} else if (processes.get(srcProcessId).isAlive() == false || (getLeader(srcProcessId) != null && processes.get(getLeader(srcProcessId)).isAlive() == false )) {
			throw new Exception("the process or its leader is not alive. inducing an exception");
		}else {
			System.out.println("NO BREAAAAAAAAAAAAcH"+srcProcessId+","+faultMap.get(srcProcessId));
		}
		Process p = processes.get(destProcessId);
		
		updateFMap(srcProcessId, "SEND,"+msg.getMessageType());
		
		if(hasBreached(srcProcessId,"SEND,"+msg.getMessageType())) {
			throw new Exception("Send count breached after a send has been performed. inducing an exception.");
		}
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
	void run() throws Exception {
		
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
		UserInputListener ul = new UserInputListener(this, "UIL");
		addProcess("UIL", ul);
		System.out.println("spawned user input listener");
		//make clients perform requests
		int i=0;
		for(String client : clients) {
			PaxosMessage m = new PaxosMessage();
			m.setMessageType(PaxosMessageEnum.CLIENTINPUT);
			// Set this value for random msgs; Don't set it if the input is from file.
			m.setNumClientRequests((i+1)*5);
			sendMessage("main",client, m);
			i++;
		}
	}
	
	public static void main(String args[]) throws Exception {
		Main main = new Main();
		main.initFaultMap(args);
		main.run();
		
	}
}
