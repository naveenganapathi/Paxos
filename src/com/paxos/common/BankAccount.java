package com.paxos.common;

public class BankAccount {
	String accntId;
	float balance;
	
	public BankAccount(String accntId,float balance) {
		this.accntId = accntId;
		this.balance = balance;
	}
	public String getAccntId() {
		return accntId;
	}
	public void setAccntId(String accntId) {
		this.accntId = accntId;
	}
	public float getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = balance;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accntId == null) ? 0 : accntId.hashCode());
		result = prime * result + Float.floatToIntBits(balance);
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
		BankAccount other = (BankAccount) obj;
		if (accntId == null) {
			if (other.accntId != null)
				return false;
		} else if (!accntId.equals(other.accntId))
			return false;
		if (Float.floatToIntBits(balance) != Float
				.floatToIntBits(other.balance))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "BankAccount [accntId=" + accntId + ", balance=" + balance + "]";
	}
	
	
}
