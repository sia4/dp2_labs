package it.polito.dp2.NFFG.sol3.service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polito.dp2.NFFG.sol3.service.database.NffgsDB;
import it.polito.dp2.NFFG.sol3.service.generated.model.Link;
import it.polito.dp2.NFFG.sol3.service.generated.model.Nffg;
import it.polito.dp2.NFFG.sol3.service.generated.model.NodeType;
import it.polito.dp2.NFFG.sol3.service.generated.Labels;
import it.polito.dp2.NFFG.sol3.service.generated.Node;
import it.polito.dp2.NFFG.sol3.service.generated.Paths;
import it.polito.dp2.NFFG.sol3.service.generated.Property;
import it.polito.dp2.NFFG.sol3.service.generated.Relationship;

public class Neo4jWrapper {

	private WebTarget target;
	private URI uri;
	Map<String, Node> nodes = new HashMap<String, Node>();	
	private static NffgsDB db = NffgsDB.getIstance();
	
	/**
	 * Constructor
	 * @throws Neo4jServiceException 
	 */
	public Neo4jWrapper() throws Neo4jServiceException {

		try {
			uri = URI.create(System.getProperty("it.polito.dp2.NFFG.lab3.NEO4JURL", "http://localhost:8080/Neo4JXML/rest"));
			Client client = ClientBuilder.newClient();
			target = client.target(uri);

		}catch(ProcessingException ex){
			throw new Neo4jServiceException();
		
		}catch (WebApplicationException ex) {
			throw new Neo4jServiceException();
		}
	
	}
	
	/**
	 * Delete all Nodes from DB 
	 */
	public void cleanDatabase() {

		Response r = target.path("resource/nodes").request().delete();
		
		if(r.getStatus() >= 400)
			throw new WebApplicationException(r.getStatus());

	}
	
	/**
	 * Load all nodes and link from the selected nffg
	 * @param nffg to load
	 * @throws Neo4jServiceException 
	 */
	public void loadNFFG(Nffg nffg_r) throws Neo4jServiceException {

		try {
			nodes.put(nffg_r.getName(), CreateNffgNode(nffg_r.getName()));
			
			nffg_r.getNodeType().stream().forEach(
					nr -> {
						Node node = CreateNode(nr);
						nodes.put(nr.getName(), node);
						addToNffg(nffg_r.getName(), node);
					});
			
			nffg_r.getLink().stream().forEach(lr -> CreateRelationship(lr));
		
		}catch (Exception ex) {
			throw new Neo4jServiceException();
		}
		
		db.getNodesMap().put(nffg_r.getName(), nodes);
	}

	private void addToNffg(String nffgName, Node node) {
		
		Relationship r = new Relationship();
		r.setType("belongs");
		String nodeSrcId = nodes.get(nffgName).getId(); 
		String nodeDestId = node.getId();
		r.setSrcNode(nodeSrcId);
		r.setDstNode(nodeDestId);

		target.path("/resource/node/"+nodeSrcId+"/relationship")
				.request(MediaType.APPLICATION_XML)
				.post(Entity.entity(r,MediaType.APPLICATION_XML),Relationship.class);		
		
	}

	/**
	 * Create the node rappresenting the Nffg
	 * @param String nffg name
	 */
	private Node CreateNffgNode(String name) {	
		Node n = new Node();
		Property p = new Property();
		p.setName("name");
		p.setValue(name);
		n.getProperty().add(p);
	
		n = target.path("/resource/node")
		.request(MediaType.APPLICATION_XML)
		.post(Entity.entity(n,MediaType.APPLICATION_XML),Node.class);
		
		Labels l = new Labels();
		l.getValue().add("NFFG");
		
		Response r = target.path("/resource/node")
				.path(n.getId())
				.path("label")
				.request(MediaType.TEXT_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(l,MediaType.APPLICATION_XML));
		
		if(r.getStatus() >= 400)
			throw new WebApplicationException(r.getStatus());
		
		return n;
	}

	/**
	 * @throws Neo4jServiceException 
	 * Create a relationship from a link
	 * @param lr the link
	 */	
	private Relationship CreateRelationship(Link lr) {

		
		Relationship r = new Relationship();
		r.setType("Link");
		String nodeSrcId = nodes.get(lr.getSource()).getId(); 
		String nodeDestId = nodes.get(lr.getDest()).getId();
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
	private Node CreateNode(NodeType nr) {

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
	 * @throws Neo4jServiceException 
	 */
	public boolean testReachability(String nffgName, String srcName, String destName)
			throws Neo4jServiceException {

		Map<String, Node> nodes = new HashMap<String, Node>();
		nodes = db.getNodesMap().get(nffgName);
		
		String srcId = nodes.get(srcName).getId();
		String destId = nodes.get(destName).getId();
		boolean test = false; 
		Paths p = target.path("resource/node/"+srcId+"/paths")
		.queryParam("dst", destId)
		.request()
		.accept(MediaType.APPLICATION_XML)
		.get(Paths.class);

		test = p.getPath() == null ? false : true;
		
		return test;
	}

	/**
	 * Delete the Nffg
	 * @param nffg
	 */
	public void deleteNFFG(Nffg nffg) {
		
		Map<String, Node> nodes = new HashMap<String, Node>();
		nodes = db.getNodesMap().get(nffg.getName());
		
		for(Node n : nodes.values()) {
			Response r = target.path("resource/node"+n.getId()).request().delete();
			
			if(r.getStatus() >= 400)
				throw new InternalServerErrorException();
		}
		
	}

}
