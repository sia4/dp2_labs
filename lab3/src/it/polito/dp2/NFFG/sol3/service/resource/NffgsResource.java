package it.polito.dp2.NFFG.sol3.service.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import it.polito.dp2.NFFG.sol3.service.NffgService;
import it.polito.dp2.NFFG.sol3.service.generated.model.Nffgs;

@Path("/nffgs")
@Api(value = "/nffgs", description = "A collection of Nffgs")
public class NffgsResource {

	NffgService service = new NffgService();
    @GET 
    @ApiOperation(value = "Get all the Nffgs")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 204, message = "The service is empty"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces( { MediaType.APPLICATION_XML})
    public Nffgs getNffgs() {
    	 	
    	return service.getAllNffgs();
    }
    
}
