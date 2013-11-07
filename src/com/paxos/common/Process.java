package com.paxos.common;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

import com.paxos.Main;

public abstract class Process extends Thread{

	public String processId;
	public Main main;
	public Queue<PaxosMessage> messages=new Queue<PaxosMessage>();
	public PrintWriter writer;
	
	public void run(){
		body();
		main.removeProcess(processId);
	}
	

	abstract public void body();
	
	public void initwriter(String procId) {
		try {
			Random r = new Random();
			this.writer = new PrintWriter(new FileWriter("C:\\Users\\vignesh\\git\\Paxos\\"+"tt"+r.nextInt(1000)+".txt", true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeToLog(String s) {
		System.out.println(s);
		writer.println(processId+" : "+s);
		writer.flush();
	}
	public void sendMessage(String destProcessId, PaxosMessage msg){
		main.sendMessage(destProcessId, msg);
	}
	
	public void deliver(PaxosMessage msg) {
		messages.enqueue(msg);
	}
	
	public PaxosMessage getNextMessage(){
		return messages.dequeue();
	}
	
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
}
