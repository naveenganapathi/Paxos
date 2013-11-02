package com.paxos.common;

public class Ballot {
	int ballotId;
	String processId;
	public Ballot(String processId, int ballotId) {
		this.ballotId=ballotId;
		this.processId=processId;
	}
	public int getBallotId() {
		return ballotId;
	}
	public void setBallotId(int ballotId) {
		this.ballotId = ballotId;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}	
	/** a.compareWith(b)
	 * Returns zero if equal, negative value if a<b, positive value if a>b
	 * @param ballot
	 * @return
	 */
	public int compareWith(Ballot ballot) {
		if(this.ballotId == ballot.ballotId) {
			return this.processId.compareTo(ballot.getProcessId());
		} else {
			if(this.ballotId < ballot.ballotId)
				return -1;
			else
				return 1;
		}
	}
	@Override
	public String toString() {
		return "Ballot [ballotId=" + ballotId + ", processId=" + processId
				+ "]";
	}
}
