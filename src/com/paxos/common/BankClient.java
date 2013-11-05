package com.paxos.common;

import java.util.List;

public class BankClient {
	String clientId;
	List<BankAccount> accounts;
	
	public BankClient (String clientId, List<BankAccount> accnts) {
		this.clientId = clientId;
		this.accounts = accnts;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public List<BankAccount> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<BankAccount> accounts) {
		this.accounts = accounts;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accounts == null) ? 0 : accounts.hashCode());
		result = prime * result
				+ ((clientId == null) ? 0 : clientId.hashCode());
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
		BankClient other = (BankClient) obj;
		if (accounts == null) {
			if (other.accounts != null)
				return false;
		} else if (!accounts.equals(other.accounts))
			return false;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "BankClient [clientId=" + clientId + ", accounts=" + accounts
				+ "]";
	}
	
	
}
