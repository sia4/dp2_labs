package it.polito.dp2.NFFG.sol1;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.TraversalPolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;

public class TraversalPolicy extends Policy implements TraversalPolicyReader {

	private String name;
	private NffgReader nffg;
	private boolean value;
	private VerificationResultReader result = null;
	private NodeReader destNode;
	private NodeReader sourceNode;
	private List<FunctionalType> traversedFuctionalTypes = new ArrayList<>();
	
	public TraversalPolicy(String name, NffgReader nffgReader, NodeReader sourceNode, NodeReader destNode, boolean value) {
		this.name = name;
		this.nffg = nffgReader;
		this.destNode = destNode;
		this.sourceNode = sourceNode;
		this.value = value;
	}
	
	public void SetResult(VerificationResultReader result) {
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

	public void addTraversedFuctionalTypes(FunctionalType f){
		traversedFuctionalTypes.add(f);
	}
	@Override
	public Set<FunctionalType> getTraversedFuctionalTypes() {
		return traversedFuctionalTypes.stream().collect(Collectors.toSet());
	}

}
