package com.paxos;

import java.util.HashMap;
import java.util.Map;

import com.paxos.common.PaxosMessage;
import com.paxos.common.Process;

public class Main {
	Map<String, Process> processes=new HashMap<String, Process>();
	
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
}
