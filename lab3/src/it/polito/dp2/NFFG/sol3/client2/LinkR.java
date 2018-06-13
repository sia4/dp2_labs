package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.NodeReader;

public class LinkR implements it.polito.dp2.NFFG.LinkReader{

	private String name;
	private NodeReader source;
	private NodeReader dest;
	
	public LinkR(String name, NodeReader source, NodeReader dest) {
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
