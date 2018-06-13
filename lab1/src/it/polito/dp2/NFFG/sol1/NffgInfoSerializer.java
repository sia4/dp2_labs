package it.polito.dp2.NFFG.sol1;

import java.io.File;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;

public class NffgInfoSerializer {
	private it.polito.dp2.NFFG.NffgVerifier monitor;

	private File file;
	
	/**
	 * Default constructor
	 * @throws NffgVerifierException 
	 */
	public NffgInfoSerializer(String file) throws NffgVerifierException {
		it.polito.dp2.NFFG.NffgVerifierFactory factory = it.polito.dp2.NFFG.NffgVerifierFactory.newInstance();
		monitor = factory.newNffgVerifier();
		this.file = new File(file);
	}
	
	public NffgInfoSerializer(NffgVerifier monitor, String file) {
		super();
		this.monitor = monitor;
		this.file = new File(file);
	}
	
	private void serialize() {	
        try {
			JAXBContext jc = JAXBContext.newInstance("it.polito.dp2.NFFG.sol1.jaxb");
	        ServiceProvider sp = new ServiceProvider();
	        
		    serializeNffgs(sp);
		    serializePolicies(sp);

            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            m.marshal( sp, file );
            
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}	
	
	private void serializePolicies(ServiceProvider sp) {
		Set<PolicyReader> set = monitor.getPolicies();
		
		for (PolicyReader pr: set) {
					
			PolicyType p = new PolicyType();
				
			p.setName(pr.getName());
			p.setValue(pr.isPositive());
			
			SetPolicyDest(p, pr);
			SetPolicySource(p, pr);
			SetPolicyType(p, pr);
			SetResult(p, pr);		
			
			NFFGType nffg = sp.getNffg().stream().filter(
					n -> 
					n.getName()
					.equals(pr.getNffg().getName()))
					.findFirst()
					.orElse(null);
			
			if(nffg != null) {
				nffg.getPolicy().add(p);
			}
		}		
		
	}

	private void SetResult(PolicyType p, PolicyReader pr) {
		
		VerificationResultReader result = pr.getResult();
		if (result != null) {
			
			ResultType res = new ResultType();
			res.setValue(result.getVerificationResult());
			try {
				res.setVTime(DatatypeFactory.newInstance()
				.newXMLGregorianCalendar((GregorianCalendar)pr.getResult().getVerificationTime()));
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
			res.setMessage(pr.getResult().getVerificationResultMsg());
			p.setResult(res);
		}
		
	}

	private void SetPolicyType(PolicyType p, PolicyReader pr) {
		if(pr instanceof TraversalPolicyReader){
			p.setType(PolicyDescType.TRAVERSAL);
			for (FunctionalType f : ((TraversalPolicyReader)pr).getTraversedFuctionalTypes()) {
				p.getTraversedFunc().add(NodeFunctionalType.fromValue(f.toString()));
			}
		} else {
			p.setType(PolicyDescType.REACHABILITY);
		}
	}

	private void SetPolicySource(PolicyType p, PolicyReader pr) {
		p.setDest(((ReachabilityPolicyReader)pr).getDestinationNode().getName());
	}

	private void SetPolicyDest(PolicyType p, PolicyReader pr) {
		p.setSource(((ReachabilityPolicyReader)pr).getSourceNode().getName());	
	}

	private void serializeNffgs(ServiceProvider sp) {
		
		Set<NffgReader> set = monitor.getNffgs();
		for (NffgReader nffg_r: set) {
			
			NFFGType nffg = new NFFGType();
			nffg.setName(nffg_r.getName());
			XMLGregorianCalendar date;
			try {
				date = DatatypeFactory.newInstance()
						.newXMLGregorianCalendar((GregorianCalendar)(nffg_r.getUpdateTime()));
				nffg.setUpdateTime(date);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}			
		
			Set<NodeReader> nodeSet = nffg_r.getNodes();
			
			for (NodeReader nr: nodeSet) {

				NodeType node = new NodeType();
				NodeFunctionalType functionalType = NodeFunctionalType.fromValue(nr.getFuncType().toString());

				node.setName(nr.getName());
				node.setNodeType(functionalType);
				
				nffg.getNode().add(node);
				
				Set<LinkReader> linkSet = nr.getLinks();
				for (LinkReader lr: linkSet) {
					LinkType link = new LinkType();
					link.setName(lr.getName());
					link.setSource(nr.getName());
					link.setDest(lr.getDestinationNode().getName());
					nffg.getLink().add(link);

				}
			}
			sp.getNffg().add(nffg);
		}		
	}

	public static void main(String[] args) {
		NffgInfoSerializer wf;
		
		if (args.length != 1) {
			System.out.println("Missing output file name.");
			return;
		}
		
		try {	
			wf = new NffgInfoSerializer(args[0]);
			wf.serialize();
		} catch (NffgVerifierException e) {
			System.err.println("Could not instantiate data generator.");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
