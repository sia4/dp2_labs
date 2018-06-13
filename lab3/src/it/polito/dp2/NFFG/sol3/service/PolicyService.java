package it.polito.dp2.NFFG.sol3.service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import it.polito.dp2.NFFG.sol3.service.database.NffgsDB;
import it.polito.dp2.NFFG.sol3.service.generated.model.Nffg;
import it.polito.dp2.NFFG.sol3.service.generated.model.Policy;
import it.polito.dp2.NFFG.sol3.service.generated.model.Result;
import it.polito.dp2.NFFG.sol3.service.generated.model.Policies;

public class PolicyService {
	
	Logger logger = Logger.getLogger(ValidationProvider.class.getName());
	private static NffgsDB db = NffgsDB.getIstance();

	public void addPolicy(Policy policy) {
		synchronized(db) {
	    	if(!db.getNffgsMap().containsKey(policy.getNffgName())) {
	    		logger.log(Level.SEVERE, "Nffg not present");
	    		throw new NotFoundException();
	    	}
	    	if(!db.getNodesMap().containsKey(policy.getNffgName())) {
	    		logger.log(Level.SEVERE, "Nffg not present");
	    		throw new NotFoundException();
	    	}
	    	
	    	if(db.getPoliciesMap().containsKey(policy.getName())) {
	    		logger.log(Level.SEVERE, "Policy already present");
	    		throw new BadRequestException();
	    	}
	    	
	    	Nffg n = db.getNffgsMap().get(policy.getNffgName());
	    	if(n.getNodeType().stream().filter(no ->
	    		no.getName().equals(policy.getSource()) ||
	    		no.getName().equals(policy.getDest())).count() != 2) {
	    		logger.log(Level.SEVERE, "Nodes not found");
	    		throw new BadRequestException();
	    	}
			db.getPoliciesMap().put(policy.getName(), policy);  
		}

	}

	public Policy getPolicy(String policyName) {
		
    	if(!db.getPoliciesMap().containsKey(policyName)) {
    		logger.log(Level.SEVERE, "Policy not present");
    		throw new NotFoundException();
    	}
    	
		return db.getPoliciesMap().get(policyName);
	}

	public void testPolicy(Policy p) {
		
		try {
			Neo4jWrapper neo;
			neo = new Neo4jWrapper();
			Boolean tested = neo.testReachability(p.getNffgName(), p.getSource(), p.getDest());

			setPolicyResult(tested, p);
		} catch (Neo4jServiceException e1) {
    		logger.log(Level.SEVERE, "Neo4jXML Error");
			throw new InternalServerErrorException("Neo4jXML Error");
		}

	}
	
	private void setPolicyResult(Boolean tested, Policy p) {
		
		Result r = new Result();
		if((tested && p.isValue()) || (!tested && !p.isValue())) {
			r.setResultValue(true);
			r.setMessage("The policy is satisfied.");
		} else {
			r.setResultValue(false);
			r.setMessage("The policy is not satisfied");
		}
		
		try {
			r.setVTime(DatatypeFactory.newInstance()
			.newXMLGregorianCalendar((GregorianCalendar)Calendar.getInstance()));
		} catch (DatatypeConfigurationException e) {
    		logger.log(Level.SEVERE, "DatatypeCpnfiguration Error");
			throw new InternalServerErrorException();
		}
		
		p.setResult(r);
	}
	
	public void updatePolicy(String id, Policy policy){
		
		synchronized(db) {
	 
	    	if(!db.getNffgsMap().containsKey(policy.getNffgName())) {
	    		logger.log(Level.SEVERE, "Nffg not present");
	    		throw new NotFoundException();
	    	}
	    	
	    	if(!db.getNodesMap().containsKey(policy.getNffgName())) {
	    		logger.log(Level.SEVERE, "Nffg not present");
	    		throw new NotFoundException();
	    	}
	    	
	    	Nffg n = db.getNffgsMap().get(policy.getNffgName());
	    	if(n.getNodeType().stream().filter(no ->
	    		no.getName().equals(policy.getSource()) ||
	    		no.getName().equals(policy.getDest())).count() != 2) {
	    		logger.log(Level.SEVERE, "Nodes not found");
	    		throw new BadRequestException();
	    	}
	    	
	    	if(!id.equals(policy.getName())) {
        		logger.log(Level.SEVERE, "Id already present");
        		throw new NotFoundException(); 
	    	}
	
			db.getPoliciesMap().put(policy.getName(), policy);  
		}
	
	}
	
	public Response deletePolicy(String id) {
		
		synchronized(db) {
	    	if(!db.getPoliciesMap().containsKey(id)) {
        		logger.log(Level.SEVERE, "Policy not present");
	    		throw new NotFoundException();
	    	} else {
		    	db.getPoliciesMap().remove(id);       	
		    	return Response.status(200).build();
	    	}
		}
		
	}
	
	public Policies getAllPolicies() {
		
		Policies policies = new Policies();
		policies.getPolicy().addAll(db.getPoliciesMap().values());

		return policies;
		
	}
	
}
