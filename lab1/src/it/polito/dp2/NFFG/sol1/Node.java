package it.polito.dp2.NFFG.sol1;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NodeReader;

public class Node implements NodeReader {

	private String name;
	private FunctionalType fType;
	private HashMap<String, LinkReader> links = new HashMap<>();
	
	public Node(String name, FunctionalType fType) {
		this.name = name;
		this.fType = fType;
	}
	
	public void addLink(LinkReader link){
		links.put(link.getName(), link);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public FunctionalType getFuncType() {
		return fType;
	}

	@Override
	public Set<LinkReader> getLinks() {
		return links.values().stream().collect(Collectors.toSet());
	}

}
