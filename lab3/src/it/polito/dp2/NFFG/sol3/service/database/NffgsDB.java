package it.polito.dp2.NFFG.sol3.service.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFFG.sol3.service.Neo4jServiceException;
import it.polito.dp2.NFFG.sol3.service.Neo4jWrapper;
import it.polito.dp2.NFFG.sol3.service.generated.Node;
import it.polito.dp2.NFFG.sol3.service.generated.model.Nffg;
import it.polito.dp2.NFFG.sol3.service.generated.model.Policy;

public class NffgsDB {
	
	private Map<String, Nffg> nffgsMap = new ConcurrentHashMap<String, Nffg>();
	private Map<String, Policy> policiesMap = new ConcurrentHashMap<String, Policy>();
	private Map<String, Map<String,Node>> nodesMap = new ConcurrentHashMap<String, Map<String,Node>>();
	
	private static NffgsDB istance;
	
	private NffgsDB() {
		
	}
	
	public static NffgsDB getIstance() {
		if(istance == null) {
			istance = new NffgsDB();
			try {
				Neo4jWrapper neo;
				neo = new Neo4jWrapper();
				neo.cleanDatabase();
			} catch (Neo4jServiceException e1) {	
				throw new InternalServerErrorException("Neo4jXML Error");
			}
		}
		
		return istance;
	}
	public Map<String, Nffg> getNffgsMap() {
		return nffgsMap;
	}

	public void setNffgsMap(Map<String, Nffg> map) {
		nffgsMap = map;
	}

	public Map<String, Policy> getPoliciesMap() {
		return policiesMap;
	}

	public void setPoliciesMap(Map<String, Policy> map) {
		policiesMap = map;
	}
	
	public Map<String, Map<String, Node>> getNodesMap() {
		return nodesMap;
	}

	public void setNodesMap(Map<String, Map<String, Node>> map) {
		nodesMap = map;
	}
}

