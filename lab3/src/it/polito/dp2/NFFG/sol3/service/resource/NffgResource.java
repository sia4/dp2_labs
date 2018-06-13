package it.polito.dp2.NFFG.sol3.service.resource;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import it.polito.dp2.NFFG.sol3.service.NffgService;
import it.polito.dp2.NFFG.sol3.service.generated.model.Nffg;

@Path("/nffg")
@Api(value = "/nffg", description = "A single Nffg")
public class NffgResource {

	NffgService service = new NffgService();
    @POST 
    @ApiOperation(value = "Implements a new Nffg", notes = "create a new Nffg on the server")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "Nffg created"),
    		@ApiResponse(code = 500, message = "Error generating Nffg")})
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response putNffg(Nffg nffg, @Context UriInfo uriInfo) {

    	Nffg n =  service.addNffg(nffg);
    	
    	UriBuilder builder = uriInfo.getAbsolutePathBuilder();
    	URI u = builder.path(n.getName()).build();
    	return Response.created(u).entity(n).build();
    }

    @GET
    @Path("{id}")
    @ApiOperation(	value = "Get a single Nffg")
    	    @ApiResponses(value = {
    	    		@ApiResponse(code = 200, message = "OK"),
    	    		@ApiResponse(code = 404, message = "Nffg Not Found"),
    	    		@ApiResponse(code = 500, message = "Internal Server Error")})
    	    @Produces({MediaType.APPLICATION_XML})
    public Nffg getNffg(@PathParam("id") String id ) {
    	
    	return service.getNffg(id);
    }

}
