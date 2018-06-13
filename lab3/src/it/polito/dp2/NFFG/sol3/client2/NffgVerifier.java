package it.polito.dp2.NFFG.sol3.client2;

import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NffgVerifierException;
import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.sol3.client2.generated.Link;
import it.polito.dp2.NFFG.sol3.client2.generated.Nffg;
import it.polito.dp2.NFFG.sol3.client2.generated.Nffgs;
import it.polito.dp2.NFFG.sol3.client2.generated.NodeType;
import it.polito.dp2.NFFG.sol3.client2.generated.Policies;
import it.polito.dp2.NFFG.sol3.client2.generated.Policy;

public class NffgVerifier implements it.polito.dp2.NFFG.NffgVerifier {

	private URI uri;
	private WebTarget target;

	public NffgVerifier() throws NffgVerifierException {
		uri = URI.create(System.getProperty("it.polito.dp2.NFFG.lab3.URL", "http://localhost:8080/NffgService/rest"));
		Client client = ClientBuilder.newClient();
		target = client.target(uri);

	}

	@Override
	public NffgReader getNffg(String arg0) {
		
		Nffg n = target.path("nffg")
		.path(arg0)		
		.request()
		.accept(MediaType.APPLICATION_XML)
		.get(Nffg.class);
		
		Calendar c = n.getUpdateTime().toGregorianCalendar();
		NffgR n_r = new NffgR(n.getName(), c);
		HashMap<String, NodeR> nodes = new HashMap<>();
		
		for(NodeType node : n.getNodeType()) {
			FunctionalType ft = FunctionalType.fromValue(node.getFunctionalType());
			NodeR node_r = new NodeR(node.getName(), ft);
			n_r.addNode(node_r);
			nodes.put(node_r.getName(), node_r);
		}
		
		for(Link link : n.getLink()) {
			LinkR link_r = new LinkR(link.getName(), n_r.getNode(link.getSource()),
					n_r.getNode(link.getDest()));
			((NodeR)n_r.getNode(link.getSource())).addLink(link_r);
			//nodes.get().addLink(link_r);
			
		}

		return n_r;
	}

	@Override
	public Set<NffgReader> getNffgs() {
		
		Nffgs ns = target.path("nffgs")		
		.request()
		.accept(MediaType.APPLICATION_XML)
		.get(Nffgs.class);
		
		HashMap<String, NodeR> nodes = new HashMap<>();
		
		Set<NffgReader> nffgs_r = new HashSet<>();
		for(Nffg n : ns.getNffg()) {
			Calendar c = n.getUpdateTime().toGregorianCalendar();
			NffgR n_r = new NffgR(n.getName(), c);
			
			for(NodeType node : n.getNodeType()) {
				FunctionalType ft = FunctionalType.fromValue(node.getFunctionalType());
				NodeR node_r = new NodeR(node.getName(), ft);
				n_r.addNode(node_r);
				nodes.put(node_r.getName(), node_r);
			}
			
			for(Link link : n.getLink()) {
				LinkR link_r = new LinkR(link.getName(), n_r.getNode(link.getSource()),
						n_r.getNode(link.getDest()));
				nodes.get(link.getSource()).addLink(link_r);
				
			}
			nffgs_r.add(n_r);
		}
		
		return nffgs_r;
	}

	@Override
	public Set<PolicyReader> getPolicies() {
		Policies policies = target.path("policies")		
		.request()
		.accept(MediaType.APPLICATION_XML)
		.get(Policies.class);
		
		Set<PolicyReader> policies_r = new HashSet<>();
		for(Policy p : policies.getPolicy()) {
			NffgReader nr = getNffg(p.getNffgName());
			
			ReachabilityPolicyR re_r = new ReachabilityPolicyR(p.getName(), nr,
					nr.getNode(p.getSource()),
					nr.getNode(p.getDest()), p.isValue());
			
			if (p.getResult() != null) {

				Calendar t = (Calendar) p.getResult().getVTime().toGregorianCalendar();
				re_r.setResult(new VerificationResult(re_r, p.getResult().isResultValue(), p.getResult().getMessage(), t));

			}
			policies_r.add(re_r);
			
		}
		return policies_r;
	}

	@Override
	public Set<PolicyReader> getPolicies(String nffgName) {
;
		
		Policies policies = target.path("policies")		
		.request()
		.accept(MediaType.APPLICATION_XML)
		.get(Policies.class);

		NffgReader nr = getNffg(nffgName);
		
		Set<PolicyReader> policies_r = new HashSet<>();
		for(Policy p : policies.getPolicy()) {

			if(p.getNffgName().equals(nffgName)) {
				ReachabilityPolicyR re_r = new ReachabilityPolicyR(p.getName(), nr,
						nr.getNode(p.getSource()),
						nr.getNode(p.getDest()), p.isValue());
				if (p.getResult() != null) {
	
					Calendar t = (Calendar) p.getResult().getVTime().toGregorianCalendar();
					re_r.setResult(new VerificationResult(re_r, p.getResult().isResultValue(), p.getResult().getMessage(), t));
		
				}
				
				policies_r.add(re_r);
			}
		}
		return policies_r;
	}

	@Override
	public Set<PolicyReader> getPolicies(Calendar arg0) {
		Policies policies = target.path("policies")		
		.request()
		.accept(MediaType.APPLICATION_XML)
		.get(Policies.class);
		
		Set<PolicyReader> policies_r = new HashSet<>();
		for(Policy p : policies.getPolicy()) {
			
			if (p.getResult() != null) {
				Calendar t = (Calendar) p.getResult().getVTime().toGregorianCalendar();
				if(t.equals(arg0)) {
	
					NffgReader nr = getNffg(p.getNffgName());
					ReachabilityPolicyR re_r = new ReachabilityPolicyR(p.getName(), nr,
							nr.getNode(p.getSource()),
							nr.getNode(p.getDest()), p.isValue());
	
					re_r.setResult(new VerificationResult(re_r, p.getResult().isResultValue(), p.getResult().getMessage(), t));
					policies_r.add(re_r);
				}
					
			}
			

		}

		return policies_r;
	}
	
	public static void main(String[] args) throws NffgVerifierException {
		System.setProperty("it.polito.dp2.NFFG.NffgVerifierFactory", "it.polito.dp2.NFFG.sol3.client2.NffgVerifierFactory");
		it.polito.dp2.NFFG.sol3.client2.NffgVerifierFactory factory = (NffgVerifierFactory) it.polito.dp2.NFFG.sol3.client2.NffgVerifierFactory
				.newInstance();
		
		NffgVerifier v = factory.newNffgVerifier();

		for(NffgReader n : v.getNffgs())
			System.out.println(n.getName());

		for(PolicyReader p : v.getPolicies())
			System.out.println(p.getName());
		
		for(PolicyReader p : v.getPolicies("Nffg1"))
			System.out.println("banana" + p.getName());

		
	}
}
