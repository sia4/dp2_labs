package it.polito.dp2.NFFG.sol3.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import it.polito.dp2.NFFG.sol3.service.generated.model.Policy;

@Provider
@Consumes({"application/xml","text/xml"})
public class PolicyValidationProvider extends ValidationProvider<Policy>{

}
