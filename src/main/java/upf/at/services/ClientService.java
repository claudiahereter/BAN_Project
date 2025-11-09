package upf.at.services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import upf.at.data.Clients;


@Path("/client")
public class ClientService {
    private static List<Clients> clients = new ArrayList<>();

    public static List<Clients> getClients() {
        return clients;
    }

    @POST
    @Path("/subscribe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribeClient(Clients c) {
    	String phone =  c.getPhone();
    	String token = c.getTelegramToken();
    	List<Integer> stationIds =  c.getStationsIds();
    	
    	//
        if (c == null || phone == null || phone.trim().isEmpty() || 
            token == null || token.trim().isEmpty() ||
            stationIds == null || stationIds.isEmpty()) {
            
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid input: Phone, token, and station IDs are required.\"}")
                    .build();
        }

        // Validate phone number format
        Boolean validPhone = NotifierService.validatePhone(phone);
		if(!validPhone)
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\": \"Please enter a valid phone number (7-15 digits)\"}")
					.build();
		// Validate token format
		if(!validateTelegramToken(token)) return Response.status(Response.Status.UNAUTHORIZED)
				.entity("{\"error\": \"Please enter a valid telegram token\"}")
				.build();
		
		// Validate station IDs (all must be numbers)
        for (Integer stationId : stationIds) {
            if (stationId == null || stationId < 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Invalid station IDs\"}")
                        .build();
            }
        }
        
        
	    // Find client by phone number
		Clients client = NotifierService.getClient(phone);
		if (client != null) {
	        return Response.status(Response.Status.CONFLICT)
	                .entity("{\"error\": \"This phone number is already subscribed\"}")
	                .build();
	    }
		
		
        try {
            Clients clientData = new Clients(c.getPhone(), c.getTelegramToken(), c.getStationsIds());
            clients.add(clientData);
            return Response.status(Response.Status.OK)
                    .entity("{\"success\": \"Client successfully subscribed!\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An error occurred while subscribing the client\"}")
                    .build();
        }
    }


    @GET
    @Path("/list")
	@Produces(MediaType.APPLICATION_JSON) 
    public List<Clients> get() {
		
		//clients.add(clientData);
        return clients;
    }
	



////////// AUXILIARY FUNCTION ////////////////////////////////////

	public Boolean validateTelegramToken(String botToken) {
	    Client client = ClientBuilder.newClient();
	    WebTarget target = client.target("https://api.telegram.org")
	            .path("/bot" + botToken + "/getMe");
	
	    Response response = target.request().get();
	    int status = response.getStatus();
	    response.close();
	
	    return status == 200; 
	}
	
}
