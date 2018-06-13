package it.polito.dp2.NFFG.sol2;

import it.polito.dp2.NFFG.NffgVerifierException;
import it.polito.dp2.NFFG.lab2.ReachabilityTesterException;
import it.polito.dp2.NFFG.lab2.ServiceException;

public class ReachabilityTesterFactory extends it.polito.dp2.NFFG.lab2.ReachabilityTesterFactory {

	@Override
	public ReachabilityTester newReachabilityTester() throws ReachabilityTesterException {
		
		try {
			return new ReachabilityTester();
		} catch (NffgVerifierException e) {
			e.getStackTrace();
			throw new ReachabilityTesterException();
		} catch (ServiceException e) {
			e.getStackTrace();
			throw new ReachabilityTesterException();
		}
	}

}
