package com.paxos.common;

import com.paxos.Main;

public abstract class Process extends Thread{
	protected String processId;
	protected Main main;
	Queue<PaxosMessage> messages=new Queue<PaxosMessage>();
	
	public void run(){
		body();
		main.removeProcess(processId);
	}
	

	abstract public void body();
	
	public void sendMessage(String destProcessId, PaxosMessage msg){
		main.sendMessage(destProcessId, msg);
	}
	
	public void deliver(PaxosMessage msg) {
		messages.enqueue(msg);
	}
	
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
}
