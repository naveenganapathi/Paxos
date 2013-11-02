package com.paxos.common;

public class Request {
	String clientId;
	int clientCommandId;
	String command;
	public Request(String clientId, int clientCommandId, String command) {
		this.clientId=clientId;
		this.clientCommandId=clientCommandId;
		this.command=command;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public int getClientCommandId() {
		return clientCommandId;
	}
	public void setClientCommandId(int clientCommandId) {
		this.clientCommandId = clientCommandId;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String operation) {
		this.command = operation;
	}
	@Override
	public String toString() {
		return "Request [clientId=" + clientId + ", clientCommandId="
				+ clientCommandId + ", command=" + command + "]";
	}
}
