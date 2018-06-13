package it.polito.dp2.NFFG.sol3.client2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import it.polito.dp2.NFFG.NodeReader;

public class NffgR implements it.polito.dp2.NFFG.NffgReader{

	private String name;
	private Calendar updateTime;
	private HashMap<String, NodeReader> nodes = new HashMap<>();

	public NffgR(String name, Calendar updateTime) {
		this.name = name;
		this.updateTime = updateTime;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public NodeReader getNode(String arg0) {
		return nodes.get(arg0);
	}

	@Override
	public Set<NodeReader> getNodes() {
		return nodes.values().stream().collect(Collectors.toSet());
	}

	@Override
	public Calendar getUpdateTime() {
		return updateTime;
	}

	public void addNode(NodeR node_r) {
		nodes.put(node_r.getName(), node_r);	
	}

}
