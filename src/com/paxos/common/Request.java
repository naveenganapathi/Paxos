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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + clientCommandId;
		result = prime * result
				+ ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request other = (Request) obj;
		if (clientCommandId != other.clientCommandId)
			return false;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (command == null) {
			if (other.command != null)
				return false;
		} else if (!command.equals(other.command))
			return false;
		return true;
	}
	
	
}
