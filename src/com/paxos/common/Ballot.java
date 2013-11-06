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
		Ballot bn = ( Ballot ) ballot ;
		if ( bn.ballotId != ballotId ) {
		return ballotId - bn.ballotId ;
		}
		return  processId.compareTo(bn.processId);

	}
	public String getString(){
		return processId+","+ballotId;
	}
	@Override
	public String toString() {
		return "Ballot [ballotId=" + ballotId + ", processId=" + processId
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ballotId;
		result = prime * result
				+ ((processId == null) ? 0 : processId.hashCode());
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
		Ballot other = (Ballot) obj;
		if (ballotId != other.ballotId)
			return false;
		if (processId == null) {
			if (other.processId != null)
				return false;
		} else if (!processId.equals(other.processId))
			return false;
		return true;
	}
	
	
}
