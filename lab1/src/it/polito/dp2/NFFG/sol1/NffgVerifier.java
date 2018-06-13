package it.polito.dp2.NFFG.sol1;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;

public class NffgVerifier implements it.polito.dp2.NFFG.NffgVerifier {

	private ServiceProvider sp;
	private HashMap<String, NffgReader> nffgs = new HashMap<>();
	private HashMap<String, PolicyReader> policies = new HashMap<>();

	public NffgVerifier() throws NffgVerifierException {

		try {
			File file = new File(System.getProperty("it.polito.dp2.NFFG.sol1.NffgInfo.file"));
			JAXBContext jc;
			jc = JAXBContext.newInstance("it.polito.dp2.NFFG.sol1.jaxb");
			Unmarshaller u = jc.createUnmarshaller();
			SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);

			try {
				Schema schema = sf.newSchema(new File("xsd/nffgInfo.xsd"));
				u.setSchema(schema);
			} catch (org.xml.sax.SAXException se) {
				throw new NffgVerifierException();
			}

			sp = (ServiceProvider) u.unmarshal(file);

			for (NFFGType n : sp.getNffg()) {

				Nffg nr = new Nffg(n.getName(), n.getUpdateTime().toGregorianCalendar());
				nffgs.put(n.getName(), nr);

				for (NodeType no : n.getNode()) {
					FunctionalType ft = getFunctionalType(no.getNodeType());
					Node nor = new Node(no.getName(), ft);
					nr.addNode(nor);

				}

				for (LinkType l : n.getLink()) {
					Link lr = new Link(l.getName(), nr.getNode(l.getSource()), nr.getNode(l.getDest()));
					Node n2 = (Node) nr.getNode(l.getSource());
					n2.addLink((LinkReader) lr);
				}

				for (PolicyType p : n.getPolicy()) {
					Policy pr;
					if (p.getType() == PolicyDescType.TRAVERSAL) {
						pr = new TraversalPolicy(p.getName(), nr,
							nr.getNode(p.getSource()), nr.getNode(p.getDest()),
							p.isValue());

						if(p.getTraversedFunc().isEmpty())
							throw new NffgVerifierException();
						
						for (NodeFunctionalType f : p.getTraversedFunc()) {
							((TraversalPolicy) pr).addTraversedFuctionalTypes(getFunctionalType(f));
						}

					} else {
						pr = new ReachabilityPolicy(p.getName(), nr,
								nr.getNode(p.getSource()),
								nr.getNode(p.getDest()), p.isValue());
					}

					if (p.getResult() != null) {
						boolean r = p.getResult().isValue();

						Calendar t = (Calendar) p.getResult().getVTime().toGregorianCalendar();
						pr.SetResult(new VerificationResult(pr, r, p.getResult().getMessage(), t));
					}

					policies.put(p.getName(), pr);
				}

			}

		} catch (Exception ex) {
			throw new NffgVerifierException();
		}
	}

	private FunctionalType getFunctionalType(NodeFunctionalType nodeType) throws NffgVerifierException {

		if (FunctionalType.fromValue(nodeType.name()) == null)
			throw new NffgVerifierException();

		return FunctionalType.fromValue(nodeType.name());

	}

	@Override
	public NffgReader getNffg(String string) {
		return nffgs.get(string);
	}

	@Override
	public Set<NffgReader> getNffgs() {
		return nffgs.values().stream().collect(Collectors.toSet());
	}

	@Override
	public Set<PolicyReader> getPolicies() {
		return policies.values().stream().collect(Collectors.toSet());
	}

	@Override
	public Set<PolicyReader> getPolicies(String n) {
		return policies.values().stream().filter(p -> p.getNffg().equals(n)).collect(Collectors.toSet());
	}

	@Override
	public Set<PolicyReader> getPolicies(Calendar c) {
		return policies.values().stream().filter(p -> p.getResult().getVerificationTime().equals(c))
				.collect(Collectors.toSet());
	}

	public static void main(String[] args) throws NffgVerifierException {

		System.setProperty("it.polito.dp2.NFFG.NffgVerifierFactory", "it.polito.dp2.NFFG.sol1.NffgVerifierFactory");

		System.setProperty("it.polito.dp2.NFFG.sol1.NffgInfo.file",
				"D:\\POLITECNICO\\MAGISTRALE\\SECONDO ANNO\\DISTRIBUTED PROGRAMMING II\\workspace2\\Assignement1_DP2\\file.xml");
		it.polito.dp2.NFFG.sol1.NffgVerifierFactory factory = (NffgVerifierFactory) it.polito.dp2.NFFG.sol1.NffgVerifierFactory
				.newInstance();
		it.polito.dp2.NFFG.sol1.NffgVerifier monitor = factory.newNffgVerifier();

		Set<PolicyReader> set = monitor.getPolicies("prova");

		if (set == null)
			System.out.println("null");

		System.out.println(set);
	}

}
