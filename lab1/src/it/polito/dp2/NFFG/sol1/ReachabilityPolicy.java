package it.polito.dp2.NFFG.sol1;

import it.polito.dp2.NFFG.*;

public class ReachabilityPolicy  extends Policy implements it.polito.dp2.NFFG.ReachabilityPolicyReader{

	private String name;
	private boolean value;
	private VerificationResultReader result = null;
	private NodeReader destNode;
	private NodeReader sourceNode;
	private NffgReader nffg;
	
	public ReachabilityPolicy(String name,  NffgReader nffg, NodeReader sourceNode, NodeReader destNode, boolean value) {
		this.name = name;
		this.destNode = destNode;
		this.sourceNode = sourceNode;
		this.nffg = nffg;
		this.value = value;
	}
	
	public void SetResult(VerificationResultReader result) {
		this.result = result; 
	}

	@Override
	public VerificationResultReader getResult() {
		return result;
	}

	@Override
	public Boolean isPositive() {
		return value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public NodeReader getDestinationNode() {
		return destNode;
	}

	@Override
	public NodeReader getSourceNode() {
		return sourceNode;
	}

	@Override
	public NffgReader getNffg() {
		return nffg;
	}

}
