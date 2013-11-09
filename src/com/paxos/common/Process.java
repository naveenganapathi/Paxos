package com.paxos.common;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import com.paxos.Main;

public abstract class Process extends Thread{

	public String processId;
	public Main main;
	public Queue<PaxosMessage> messages=new Queue<PaxosMessage>();
	public PrintWriter writer;
	public boolean alive;
	
	public Process() {
		this.alive = true;
	}
	public void run(){
		try {
			body();
		} catch (Exception e) {
			writeToLog("caught exception. finishing execution."+e);
			e.printStackTrace();
		}
		main.removeProcess(processId);
	}
	

	abstract public void body() throws Exception;
	
	public void initwriter(String procId) {
	}
	
	public void writeToLog(String s) {
		String temp = processId.replace(":", "_").replace(",","_");
		try {
			this.writer = new PrintWriter(new FileWriter("C:\\Users\\vignesh\\git\\Paxos\\"+temp+".txt", true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(s);
		writer.println(processId+" : "+s);
		writer.close();		
	}
	public void sendMessage(String destProcessId, PaxosMessage msg) throws Exception{
		main.sendMessage(this.processId,destProcessId, msg);
	}
	
	public void deliver(PaxosMessage msg) {
		messages.enqueue(msg);
	}
	
	public PaxosMessage getNextMessage() throws Exception{
		PaxosMessage msg = messages.dequeue();
		main.updateFMap(this.processId, "RECEIVE,"+msg.getMessageType());
		
		if(main.hasBreached(this.processId,"RECEIVE,"+msg.getMessageType())) {
			throw new Exception("Send count breached after a send has been performed. inducing an exception.");
		} else if (main.processes.get(this.processId).isAlive() == false || (main.getLeader(this.processId) != null && main.processes.get(main.getLeader(this.processId)).isAlive() == false )) {
			throw new Exception("the process or its leader is not alive. inducing an exception");
		}
		return msg;
	}
	
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
}
