package com.paxos.common;

public class BankCommand {
	CommandEnum commandEnum;
	String srcAccntId;
	String destAccntId;
	float amt;
	
	public BankCommand(CommandEnum e, String src, String dst, float amt) {
		this.commandEnum = e;
		this.srcAccntId = src;
		this.destAccntId = dst;
		this.amt = amt;
	}
	public CommandEnum getCommandEnum() {
		return commandEnum;
	}
	public void setCommandEnum(CommandEnum commandEnum) {
		this.commandEnum = commandEnum;
	}
	public String getSrcAccntId() {
		return srcAccntId;
	}
	public void setSrcAccntId(String srcAccntId) {
		this.srcAccntId = srcAccntId;
	}
	public String getDestAccntId() {
		return destAccntId;
	}
	public void setDestAccntId(String destAccntId) {
		this.destAccntId = destAccntId;
	}
	public float getAmt() {
		return amt;
	}
	public void setAmt(float amt) {
		this.amt = amt;
	}
	@Override
	public String toString() {
		return "BankCommand [commandEnum=" + commandEnum + ", srcAccntId="
				+ srcAccntId + ", destAccntId=" + destAccntId + ", amt=" + amt
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(amt);
		result = prime * result
				+ ((commandEnum == null) ? 0 : commandEnum.hashCode());
		result = prime * result
				+ ((destAccntId == null) ? 0 : destAccntId.hashCode());
		result = prime * result
				+ ((srcAccntId == null) ? 0 : srcAccntId.hashCode());
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
		BankCommand other = (BankCommand) obj;
		if (Float.floatToIntBits(amt) != Float.floatToIntBits(other.amt))
			return false;
		if (commandEnum != other.commandEnum)
			return false;
		if (destAccntId == null) {
			if (other.destAccntId != null)
				return false;
		} else if (!destAccntId.equals(other.destAccntId))
			return false;
		if (srcAccntId == null) {
			if (other.srcAccntId != null)
				return false;
		} else if (!srcAccntId.equals(other.srcAccntId))
			return false;
		return true;
	}
	
	
}
