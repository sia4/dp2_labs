package it.polito.dp2.NFFG.sol1;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NodeReader;

public class Nffg implements NffgReader {

	private String name;
	private HashMap<String, NodeReader> nodes = new HashMap<>();
	private Calendar updateTime;
	
	public Nffg(String name, Calendar updateTime) {
		this.name = name;
		this.updateTime = updateTime;
	}
	
	public void addNode(NodeReader node){
		nodes.put(node.getName(), node);
	}
	
	@Override
	public String getName() {	
		return name;
	}

	@Override
	public NodeReader getNode(String name) {
		return nodes.get(name);
	}

	@Override
	public Set<NodeReader> getNodes() {
		return nodes.values().stream().collect(Collectors.toSet());
	}

	@Override
	public Calendar getUpdateTime() {
		return updateTime;
	}

}
