package it.polito.dp2.NFFG.sol1;

import it.polito.dp2.NFFG.*;

public class Link implements LinkReader {

	private String name;
	private NodeReader dest;
	private NodeReader source;
	
	public Link(String name, NodeReader source, NodeReader dest){
		this.name = name;
		this.source = source;
		this.dest = dest;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public NodeReader getDestinationNode() {
		return dest;
	}

	@Override
	public NodeReader getSourceNode() {
		return source;
	}

}
