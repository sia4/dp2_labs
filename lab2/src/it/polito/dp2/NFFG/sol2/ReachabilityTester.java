package it.polito.dp2.NFFG.sol2;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NffgVerifier;
import it.polito.dp2.NFFG.NffgVerifierException;
import it.polito.dp2.NFFG.NffgVerifierFactory;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.lab2.NoGraphException;
import it.polito.dp2.NFFG.lab2.ServiceException;
import it.polito.dp2.NFFG.lab2.UnknownNameException;
import it.polito.dp2.NFFG.sol2.server.Node;
import it.polito.dp2.NFFG.sol2.server.Paths;
import it.polito.dp2.NFFG.sol2.server.Property;
import it.polito.dp2.NFFG.sol2.server.Relationship;

public class ReachabilityTester implements it.polito.dp2.NFFG.lab2.ReachabilityTester {

	private NffgVerifier monitor;
	private NffgReader nffg_r = null;
	private WebTarget target;
	private TreeMap<String, Node> nodes = new TreeMap<>();
	private URI uri;
	
	/**
	 * Constructor
	 * @throws NffgVerifierException 
	 */
	public ReachabilityTester() throws NffgVerifierException, ServiceException {

		try {
			NffgVerifierFactory factory = NffgVerifierFactory.newInstance();
			monitor = factory.newNffgVerifier();
			uri = URI.create(System.getProperty("it.polito.dp2.NFFG.lab2.URL"));
			Client client = ClientBuilder.newClient();
			target = client.target(uri);

		}catch(NffgVerifierException ex){
			throw new NffgVerifierException();
			
		}catch(ProcessingException ex){
			throw new ServiceException();
		
		}catch (WebApplicationException ex) {
			throw new ServiceException();
		}
	
	}
	
	/**
	 * Delete all Nodes from DB 
	 */
	private void cleanDatabase() {

		System.out.println("Deleting all nodes...");
		Response r = target.path("resource/nodes").request().delete();
		
		if(r.getStatus() >= 400)
			throw new WebApplicationException(r.getStatus());

	}
	
	/**
	 * Load all nodes and link from the selected nffg
	 * @param nffg to load
	 * @throws UnknownNameException, ServiceException 
	 */
	@Override
	public void loadNFFG(String name) throws UnknownNameException, ServiceException {
		

		if(monitor == null)
			throw new ServiceException();
		
		nffg_r = monitor.getNffgs().stream().filter(n -> n.getName().equals(name)).findFirst().orElse(null);
	
		if(nffg_r == null)
			throw new UnknownNameException();
		
		cleanDatabase();
		
		try {
			Set<LinkReader> linkSet = new HashSet<>();
			nffg_r.getNodes().stream().forEach(
					nr -> {
						nodes.put(nr.getName(), CreateNode(nr));
						linkSet.addAll(nr.getLinks());
					});
			linkSet.stream().forEach(lr -> CreateRelationship(lr));
		
		}catch (Exception ex) {
			throw new ServiceException();
		}
		
	}

	/**
	 * @throws ServiceException 
	 * Create a relationship from a link
	 * @param lr the link
	 */	
	private Relationship CreateRelationship(LinkReader lr) {

		Relationship r = new Relationship();
		r.setType("Link");
		String nodeSrcId = nodes.get(lr.getSourceNode().getName()).getId(); 
		String nodeDestId = nodes.get(lr.getDestinationNode().getName()).getId();
		r.setSrcNode(nodeSrcId);
		r.setDstNode(nodeDestId);

		Relationship relationship = target.path("/resource/node/"+nodeSrcId+"/relationship")
				.request(MediaType.APPLICATION_XML)
				.post(Entity.entity(r,MediaType.APPLICATION_XML),Relationship.class);	
		
		return relationship;

	}
	
	/**
	 * Create a node from a node reader
	 * @param nr the node reader
	 */
	private Node CreateNode(NodeReader nr) {

		Node n = new Node();
		Property p = new Property();
		p.setName("name");
		p.setValue(nr.getName());
		n.getProperty().add(p);
	
		n = target.path("/resource/node")
		.request(MediaType.APPLICATION_XML)
		.post(Entity.entity(n,MediaType.APPLICATION_XML),Node.class);
		
		return n;
		
	}

	/**
	 * Test if from the srcName Node is possible to reach destName node
	 * @param srcName source Node name
	 * @param destName dest Node name
	 * @throws UnknownNameException, ServiceException 
	 */
	@Override
	public boolean testReachability(String srcName, String destName)
			throws UnknownNameException, ServiceException, NoGraphException {

		if(nodes.isEmpty())
			throw new NoGraphException();
		
		if(!nodes.containsKey(srcName) || !nodes.containsKey(destName))
			throw new UnknownNameException();			

		String srcId = nodes.get(srcName).getId();
		String destId = nodes.get(destName).getId();
		
			Paths p = target.path("resource/node/"+srcId+"/paths")
			.queryParam("dst", destId)
			.request()
			.accept(MediaType.APPLICATION_XML)
			.get(Paths.class);

		return p.getPath() == null ? false : true;
	}

	/**
	 * Return the name of the current graph
	 */
	@Override
	public String getCurrentGraphName() {		
		return nffg_r == null ? null : nffg_r.getName();
	}

}
