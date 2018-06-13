package it.polito.dp2.NFFG.sol3.client2;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.LinkReader;

public class NodeR implements it.polito.dp2.NFFG.NodeReader {

	private String name;
	private FunctionalType func;
	private HashMap<String, LinkReader> links = new HashMap<>();
	
	public NodeR(String name, FunctionalType func) {
		this.name = name;
		this.func = func;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public FunctionalType getFuncType() {
		return func;
	}
	
	public void addLink(LinkReader l){
		links.put(l.getName(), l);
	}

	@Override
	public Set<LinkReader> getLinks() {
		return links.values().stream().collect(Collectors.toSet());
	}

}
