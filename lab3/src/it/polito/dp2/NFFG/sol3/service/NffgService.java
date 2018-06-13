package it.polito.dp2.NFFG.sol3.service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import it.polito.dp2.NFFG.sol3.service.database.NffgsDB;
import it.polito.dp2.NFFG.sol3.service.generated.model.Nffg;
import it.polito.dp2.NFFG.sol3.service.generated.model.Nffgs;

public class NffgService {
	
	private static NffgsDB db = NffgsDB.getIstance();

	
	Logger logger = Logger.getLogger(ValidationProvider.class.getName());
	
	public Nffg addNffg(Nffg nffg) {
	
		synchronized(db) {
	    	if(db.getNffgsMap().values().stream().filter(f -> f.getName().equals(nffg.getName())).count() != 0) {
	    		logger.log(Level.SEVERE, "Nffg already present");
	    		throw new BadRequestException();
	    	}
	    	
	    	try {
				nffg.setUpdateTime(DatatypeFactory.newInstance()
				.newXMLGregorianCalendar((GregorianCalendar)Calendar.getInstance()));
			} catch (DatatypeConfigurationException e) {
	    		logger.log(Level.SEVERE, "Datatype Configuration Error");
				throw new InternalServerErrorException();
			}
	    	
			try {
				Neo4jWrapper neo;
				neo = new Neo4jWrapper();
				neo.loadNFFG(nffg);
			} catch (Neo4jServiceException e1) {	
	    		logger.log(Level.SEVERE, "Neo4jXML Error");
				throw new InternalServerErrorException("Neo4jXML Error");
			}
	
			db.getNffgsMap().put(nffg.getName(), nffg);
		}
		
    	return nffg;
		
	}
	
	public Nffg getNffg(String id) {

    	if(!db.getNffgsMap().containsKey(id)) {
    		logger.log(Level.SEVERE, "Nffg not found");
    		throw new NotFoundException();
    	} else {     	
    		return db.getNffgsMap().get(id);
    	} 
	}
	
	public Nffgs getAllNffgs() {
		Nffgs nffgs = new Nffgs();
		nffgs.getNffg().addAll(db.getNffgsMap().values());
		return nffgs;
	}
	
}
