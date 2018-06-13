package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.VerificationResultReader;

public class PolicyR implements it.polito.dp2.NFFG.PolicyReader {

	private String name;
	private NffgReader nffg;
	private boolean value;
	private VerificationResultReader result;
	
	public PolicyR(){
	}
	
	public PolicyR(String name, NffgReader nffgReader, VerificationResultReader result, boolean value) {
		this.name = name;
		this.result = result;
		this.nffg = nffgReader;
		this.value = value;
	}
	@Override
	public String getName() {
		return name;
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

}
