package it.polito.dp2.NFFG.sol1;

import it.polito.dp2.NFFG.NffgVerifierException;


public class NffgVerifierFactory extends it.polito.dp2.NFFG.NffgVerifierFactory {

	@Override
	public NffgVerifier newNffgVerifier() throws NffgVerifierException {		
		return new NffgVerifier();
	}

}
	
	
	
	
	
	
	
	