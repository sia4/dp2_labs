package it.polito.dp2.NFFG.sol3.client1;

import java.net.URI;
import java.util.GregorianCalendar;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NffgVerifier;
import it.polito.dp2.NFFG.NffgVerifierException;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.ReachabilityPolicyReader;
import it.polito.dp2.NFFG.lab3.AlreadyLoadedException;
import it.polito.dp2.NFFG.lab3.NFFGClientException;
import it.polito.dp2.NFFG.lab3.ServiceException;
import it.polito.dp2.NFFG.lab3.UnknownNameException;
import it.polito.dp2.NFFG.sol3.client1.generated.Link;
import it.polito.dp2.NFFG.sol3.client1.generated.Nffg;
import it.polito.dp2.NFFG.sol3.client1.generated.NodeType;
import it.polito.dp2.NFFG.sol3.client1.generated.Policies;
import it.polito.dp2.NFFG.sol3.client1.generated.Policy;
import it.polito.dp2.NFFG.sol3.client1.generated.Result;

public class NFFGClientImproved implements it.polito.dp2.NFFG.lab3.NFFGClient {

	private NffgVerifier monitor;
	private URI uri;
	private WebTarget target;
	
	public NFFGClientImproved() throws NFFGClientException {
		it.polito.dp2.NFFG.NffgVerifierFactory factory = it.polito.dp2.NFFG.NffgVerifierFactory.newInstance();
		try {
			monitor = factory.newNffgVerifier();
		} catch (NffgVerifierException e) {
			e.printStackTrace();
			throw new NFFGClientException();
		}
		uri = URI.create(System.getProperty("it.polito.dp2.NFFG.lab3.URL","http://localhost:8080/NffgService/rest"));
	}
	
	@Override
	public void loadNFFG(String name) throws UnknownNameException, AlreadyLoadedException, ServiceException {

		NffgReader nffg_r = monitor.getNffg(name);

		if(nffg_r == null) {
			throw new UnknownNameException();
		}
		
		Nffg nffg = new Nffg();
		nffg.setName(nffg_r.getName());
		
		XMLGregorianCalendar date;
		try {
			date = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar((GregorianCalendar)(nffg_r.getUpdateTime()));
			nffg.setUpdateTime(date);
		} catch (DatatypeConfigurationException e) {
			throw new ServiceException();
		}			
	
		for (NodeReader nr: nffg_r.getNodes()) {

			NodeType node = new NodeType();
			node.setFunctionalType(nr.getFuncType().toString());

			node.setName(nr.getName());
			
			nffg.getNodeType().add(node);

			for (LinkReader lr: nr.getLinks()) {
				Link link = new Link();
				link.setName(lr.getName());
				link.setSource(nr.getName());
				link.setDest(lr.getDestinationNode().getName());
				nffg.getLink().add(link);

			}
		}
		
		Client client = ClientBuilder.newClient();
		target = client.target(uri);

		Response r = target.path("nffg")
				.request()
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(nffg,MediaType.APPLICATION_XML));
		
		if (r.getStatus() >= 500)
			throw new ServiceException();
		if (r.getStatus() >= 400)
			throw new AlreadyLoadedException();
	}

	@Override
	public void loadAll() throws AlreadyLoadedException, ServiceException {
	
		try {
			for(NffgReader nffg_r : monitor.getNffgs()){
					loadNFFG(nffg_r.getName());
			}
			
			for(PolicyReader policy_r : monitor.getPolicies()) {
				
				ReachabilityPolicyReader p = (ReachabilityPolicyReader) policy_r;
					loadReachabilityPolicy(p.getName(), p.getNffg().getName(), p.isPositive(),
					p.getSourceNode().getName(), p.getDestinationNode().getName());
			}
			
		} catch (UnknownNameException e) {
			e.printStackTrace();
			throw new ServiceException();
		}
		
		
	}

	@Override
	public void loadReachabilityPolicy(String name, String nffgName, boolean isPositive, String srcNodeName,
			String dstNodeName) throws UnknownNameException, ServiceException {
		
		ReachabilityPolicyReader policy = (ReachabilityPolicyReader) monitor.getPolicies().stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);

		Policy p = new Policy();
		try {

			p.setName(name);
			p.setNffgName(nffgName);

			p.setValue(isPositive);

			p.setSource(srcNodeName);
			p.setDest(dstNodeName);
			
			if(policy != null && policy.getResult() != null) {
				Result result = new Result();
				result.setMessage(policy.getResult().getVerificationResultMsg());
				result.setVTime(DatatypeFactory.newInstance()
						.newXMLGregorianCalendar((GregorianCalendar) policy.getResult().getVerificationTime()));
				result.setResultValue(policy.getResult().getVerificationResult());
				p.setResult(result);
			}
			
			Client client = ClientBuilder.newClient();
			target = client.target(uri);

			Response r = target.path("policy")
				.path(name)
				.request()
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(p,MediaType.APPLICATION_XML));	
			
			if (r.getStatus() >= 500)
				throw new ServiceException();
			if (r.getStatus() >= 400)
				throw new AlreadyLoadedException();
  
		} catch (Exception ex) {
			throw new ServiceException();
		}
	}

	@Override
	public void unloadReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
			
		Client client = ClientBuilder.newClient();
		target = client.target(uri);

		Response r = target.path("policy").path(name).request().delete();
		
		if (r.getStatus() >= 500)
			throw new ServiceException();
	
	}

	@Override
	public boolean testReachabilityPolicy(String name) throws UnknownNameException, ServiceException {

		try {
			Client client = ClientBuilder.newClient();
			target = client.target(uri);

			Policies policies = target.path("policies")
					.queryParam("verify", "true")
					.queryParam("id", name)
					.request()
					.accept(MediaType.APPLICATION_XML)
					.get(Policies.class);
			
			Policy policy = policies.getPolicy().stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
			
			if(policy == null)
				throw new UnknownNameException();
			
			return policy.getResult().isResultValue();
		} catch (WebApplicationException ex) {
		    if (ex.getResponse() != null && ex.getResponse().getStatus() >= 500 ) {
		        throw new ServiceException();
		    } else if(ex.getResponse() != null && ex.getResponse().getStatus() >= 400) {
		    	throw new UnknownNameException();
		    } else {
		    	throw new ServiceException();
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ServiceException();
		}

	}

}
