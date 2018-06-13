package it.polito.dp2.NFFG.sol3.client2;

import java.util.Calendar;

import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;

public class VerificationResult implements VerificationResultReader {

	private PolicyReader policy;
	private boolean result;
	private String message;
	private Calendar verificationTime;
	
	public VerificationResult(PolicyReader pr, boolean result, String message, Calendar verificationTime) {
		this.policy = pr;
		this.result = result;
		this.message = message;
		this.verificationTime = verificationTime;
	}
	
	@Override
	public PolicyReader getPolicy() {
		return policy;
	}

	@Override
	public Boolean getVerificationResult() {
		return result;
	}

	@Override
	public String getVerificationResultMsg() {
		return message;
	}

	@Override
	public Calendar getVerificationTime() {
		return verificationTime;
	}

}
