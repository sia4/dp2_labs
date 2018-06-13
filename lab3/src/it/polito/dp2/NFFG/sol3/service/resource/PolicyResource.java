package it.polito.dp2.NFFG.sol3.service.resource;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import it.polito.dp2.NFFG.sol3.service.PolicyService;
import it.polito.dp2.NFFG.sol3.service.generated.model.Policy;

@Path("/policy")
@Api(value = "/policy", description = "A single policy")
public class PolicyResource {
  
	PolicyService service = new PolicyService();
	
    @POST 
    @ApiOperation(value = "Create a new policy")
    @ApiResponses(value = {
    		@ApiResponse(code = 201, message = "New policy created"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_XML})
    public Response postPolicy(Policy policy, @Context UriInfo uriInfo) {

    	service.addPolicy(policy); 

    	UriBuilder builder = uriInfo.getAbsolutePathBuilder();
    	URI u = builder.path(policy.getName()).build();
    	return Response.created(u).entity(policy).build();

    }
    
    @GET
    @Path("{id}")
    @ApiOperation(	value = "Get a specific policy or test it")
    	    @ApiResponses(value = {
    	    		@ApiResponse(code = 200, message = "OK"),
    	    		@ApiResponse(code = 500, message = "Internal Server Error")})
    	    @Produces({MediaType.APPLICATION_XML})
    public Policy getPolicy(@PathParam("id") String id,  @DefaultValue("false") @QueryParam("verify") String verify ) {
    	
    	Policy p = service.getPolicy(id);
    	
    	if(verify.equals("true")) {
    		service.testPolicy(p);
    	}
    	
    	return p;
    	
    }
    
    @PUT
    @Path("{id}")
    @ApiOperation(	value = "Update a policy")
    	    @ApiResponses(value = {
    	    		@ApiResponse(code = 200, message = "OK"),
    	    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_XML})
    public Response changePolicy(@PathParam("id") String id, Policy policy, @Context UriInfo uriInfo ) {   	 
    	service.updatePolicy(id, policy);
    	
    	UriBuilder builder = uriInfo.getAbsolutePathBuilder();
    	URI u = builder.path(policy.getName()).build();
    	return Response.created(u).entity(policy).build();

    }

    @DELETE
    @Path("{id}")
    @ApiOperation(	value = "Delete a policy")
    	    @ApiResponses(value = {
    	    		@ApiResponse(code = 200, message = "OK"),
    	    		@ApiResponse(code = 500, message = "Internal Server Error")})
    public Response deletePolicy(@PathParam("id") String id) {

    	return service.deletePolicy(id);
    }

}
