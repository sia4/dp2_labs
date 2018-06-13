package it.polito.dp2.NFFG.sol1;

import it.polito.dp2.NFFG.*;

public abstract class Policy implements PolicyReader  {

	private String name;
	private NffgReader nffg;
	private boolean value;
	private VerificationResultReader result = null;
	
	public Policy(){		
	}
	
	public Policy(String name, NffgReader nffgReader, boolean value) {
		this.name = name;
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
	
	public void SetResult(VerificationResultReader result) {
		this.result = result; 
	}

	@Override
	public Boolean isPositive() {
		return value;
	}

	
}
