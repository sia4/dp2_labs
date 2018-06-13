package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.VerificationResultReader;

public class ReachabilityPolicyR implements it.polito.dp2.NFFG.ReachabilityPolicyReader {

	private String name;
	private boolean value;
	private VerificationResultReader result = null;
	private NodeReader destNode;
	private NodeReader sourceNode;
	private NffgReader nffg;
	
	public ReachabilityPolicyR(String name, NffgReader nffg, NodeReader sourceNode, NodeReader destNode, boolean value) {
		this.name = name;
		this.destNode = destNode;
		this.sourceNode = sourceNode;
		this.nffg = nffg;
		this.value = value;
	}

	public void setResult( VerificationResultReader result) {
		this.result = result;
	}
	@Override
	public NffgReader getNffg() {
		return nffg;
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

}
