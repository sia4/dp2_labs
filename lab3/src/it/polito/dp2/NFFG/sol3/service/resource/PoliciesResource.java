package it.polito.dp2.NFFG.sol3.service.resource;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import it.polito.dp2.NFFG.sol3.service.PolicyService;
import it.polito.dp2.NFFG.sol3.service.generated.model.Policies;
import it.polito.dp2.NFFG.sol3.service.generated.model.Policy;

@Path("/policies")
@Api(value = "/policies", description = "A set of policies")
public class PoliciesResource {
   
	PolicyService service = new PolicyService();
    @GET
    @ApiOperation(value = "Get all policies")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces( { MediaType.APPLICATION_XML})
    public Policies getPolicies( @DefaultValue("false") @QueryParam("verify") String verify, @QueryParam("id") List<String> ids) {

    	Policies policies = new Policies();
    	if(verify.equals("true")) {
    		for(String id : ids) {
    			Policy  p = service.getPolicy(id);
    			service.testPolicy(p);
		    	policies.getPolicy().add(p);
    		}
    		return policies;
    	} else {
    		return service.getAllPolicies();
    	}

    }      
    
}
