package upf.at.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import upf.at.data.AirQuality;
import upf.at.data.AirQualityByCity;
import upf.at.data.Clients;
import upf.at.data.Message;
import upf.at.data.Notifier;
import upf.at.data.Phone;
import upf.at.data.Station;

import javax.ws.rs.client.Entity;

@Path("/notify")
public class NotifierService {
	
	@POST
	@Path("/getAirQualityFromIP")  
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)  
	public Response getAirQualityFromIP(AirQuality aqi) {
		String city = getCityFromIP(aqi.getIpAddress());

		String phone = aqi.getPhone();
		Boolean validPhone = validatePhone(phone);
		if(!validPhone)
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\": \"Please enter a valid phone number (7-15 digits)\"}")
					.build();
		
		// Find client by phone number
		Clients client = getClient(phone);
		if (client == null) {
	        return Response.status(Response.Status.NOT_FOUND)
	                .entity("{\"error\": \"This phone number is not subscribed\"}")
	                .build();
	    }

		String TelMessage = getAirQuality(city, phone);
		String jsonResponse;
	
		// Error in retrieving the Air quality
		if(TelMessage.equals("API error")) {
			jsonResponse = "{ \"error\": \"" + TelMessage + "\" }";
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(jsonResponse).build();
		}
		
		jsonResponse = "{ \"success\": \"" + TelMessage + "\" }";
		
		return Response.status(Response.Status.OK).entity(jsonResponse).build();
	};
		
	@POST
	@Path("/getAirQualityByCity")  
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)  
	public Response getAirQualityByCity(AirQualityByCity aqi) {
		String city = aqi.getCity();
		
		String phone = aqi.getPhone();
		Boolean validPhone = validatePhone(phone);
		if(!validPhone)
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\": \"Please enter a valid phone number (7-15 digits)\"}")
					.build();
		
		// Find client by phone number
		Clients client = getClient(phone);
		if (client == null) {
	        return Response.status(Response.Status.NOT_FOUND)
	                .entity("{\"error\": \"This phone number is not subscribed\"}")
	                .build();
	    }
		
		String TelMessage = getAirQuality(city, phone);
		String jsonResponse;
		
		if(TelMessage.equals("Invalid")) { return Response.status(Response.Status.BAD_REQUEST)
														.entity("{\"error\": \"Invalid city name\"}")
														.build();
		// Error in retrieving the Air quality
		}else if (!TelMessage.equals("Message sent")) {
			jsonResponse = "{ \"error\": \"" + TelMessage + "\" }";
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(jsonResponse).build();
		}
		
		jsonResponse = "{ \"success\": \"" + TelMessage + "\" }";
		return Response.status(Response.Status.OK).entity(jsonResponse).build();
		};
		
	@POST 
    @Path("/slots")  
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response notifySlots(Phone phoneRequest) {
	    
		// Validate phone number format
		Boolean validPhone = validatePhone(phoneRequest.getPhone());
		if(!validPhone)
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\": \"Please enter a valid phone number (7-15 digits)\"}")
					.build();

		// Find client by phone number
		Clients client = getClient(phoneRequest.getPhone());
		if (client == null) {
	        return Response.status(Response.Status.NOT_FOUND)
	                .entity("{\"error\": \"This phone number is not subscribed\"}")
	                .build();
	    }

		// This can not happen but just in case there is an error in the system
	    List<Integer> subscribedStations = client.getStationsIds();
	    if (subscribedStations == null || subscribedStations.isEmpty()) {
	        return Response.status(Response.Status.NOT_FOUND)
	                .entity("{\"error\": \"Client is subscribed but has no stations assigned\"}")
	                .build();
	    }
	    
	    // Fetch latest station data
	    List<Station> stations;
	    try {
	        stations = StationService.fetchStations();
	        if (stations == null || stations.isEmpty()) {
	            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
	                    .entity("{\"error\": \"Bicing API is unavailable. Please try again later\"}")
	                    .build();
	        }
	    } catch (Exception e) {
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                .entity("{\"error\": \"Failed to retrieve station data\"}")
	                .build();
	    }

		// Access to the client list to retrieve the station id list
		StringBuilder message = new StringBuilder("🚲 Available Slots:\n");
		for(Integer stationId : subscribedStations) {
			for(Station s : stations) {
				if(s.getStation_id() == stationId) {
					message.append("Station: ").append(stationId).append(": ").append(s.getNum_docks_available()).append(" free slots\n");
				}
			}
		}	
		String response = sendTelegramMessage(client.getTelegramToken(), message.toString());
		String jsonResponse;
		
		if(!response.equals("Message sent")) {
			jsonResponse = "{ \"error\": \"" + response + "\" }";
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(jsonResponse).build();
		}
		
		jsonResponse = "{ \"success\": \"" + response + "\" }";
		return Response.status(Response.Status.OK).entity(jsonResponse).build();		
	}


////////////AUXILIAR FUNCTIONS TO CHECK THE PHONE AND RETRIEVE THE CLIENT
	
	
	public static Boolean validatePhone(String phone){
	    if (phone != null) {
			// Validate phone number format
			String phonePattern = "^[0-9]{7,15}$";
			if (!phone.matches(phonePattern)) {
				return false;
			} else return true;
		}	
		return false;
	}

	public static Clients getClient(String phone){
		Clients client = null;
		for(Clients c : ClientService.getClients()) {
			if(c.getPhone().equals(phone)) {
				client = c;
				break;
			}
		}
		return client;
	}

////////////AUXILIAR FUNCTIONS FOR THE GET AIR QUALITY SERVICE
	public String getAirQuality(String city, String phone) {
		// Find client by phone number
		Clients client = null;
		for(Clients c : ClientService.getClients()) {
			if(c.getPhone().equals(phone)) {
				client = c;
				break;
			}
		}	
			
		Client clientB= ClientBuilder.newClient();
	
			WebTarget targetIP = clientB.target("https://api.waqi.info/feed/" + city + "/?token=6f304e8ad69181c7b591b5451539d084db8dafbe");
			
			// Make the GET request and get the response.
			Response response = targetIP.request(MediaType.APPLICATION_JSON).get();
			
			// Read the entity and map it to the Notifier class.
			Notifier notif = response.readEntity(Notifier.class);
			
			// If city has not a valid name
			if(notif.getData().getAqi() == 0) {
				return "Invalid";
				
			}else {
				// Prepare Telegram message
				StringBuilder message = new StringBuilder("The Air Quality of " + city + " is: " + notif.getData().getAqi() + " ");
				
				if (0 <= notif.getData().getAqi() && notif.getData().getAqi() <= 50) 
				   message.append("🟢 (Good)\n"); 
				else if (50 < notif.getData().getAqi() && notif.getData().getAqi() <= 100)  
				   message.append("🟡 (Moderate)\n"); 
				else if (100 < notif.getData().getAqi() && notif.getData().getAqi() <= 150)  
				   message.append("🟠 (Unhealthy for sensitive groups)\n"); 
				else if (150 < notif.getData().getAqi() && notif.getData().getAqi() <= 200)  
				   message.append("🔴 (Unhealthy)\n"); 
				else if (200 < notif.getData().getAqi() && notif.getData().getAqi() <= 300)  
				   message.append("🟣 (Very Unhealthy)\n"); 
				else if (300 < notif.getData().getAqi())  
				   message.append("🟤 (Hazardous)\n");  
				
				String TelMessage = sendTelegramMessage(client.getTelegramToken(), message.toString());
				return TelMessage;
			}
	}

	
	public String getCityFromIP(@PathParam("ip") String ipAddress) {
		Client client= ClientBuilder.newClient();
		WebTarget targetIP = client.target("http://ip-api.com/json").path("/"+ipAddress);
		Notifier notif = targetIP.request(MediaType.APPLICATION_JSON).get(new GenericType<Notifier>() {});
		
		return notif.getCity();
	};
	
//////////// AUXILIAR FUNCTIONS FOR SENDING TELEGRAM MESSAGES //////////////////////////////
	private String sendTelegramMessage(String botToken, String messageContent) {
		Long chatId = waitForChatId(botToken);
		Client client = ClientBuilder.newClient();
		Message message = new Message(chatId, messageContent);
		
        WebTarget target = client.target("https://api.telegram.org")
                .path("/bot" + botToken + "/sendMessage");
        Response response = target.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(message, MediaType.APPLICATION_JSON));
        
       if (response.getStatus() == 200) {
           return "Message sent";
       } else {
           return "Error sending message, Telegram API is unavailable"; 
       }
	}
	
	private static Long waitForChatId(String BOT_TOKEN) {
	    Long chatId = null;
	    int attempts = 0;
	    int maxAttempts = 12; // 1 minute timeout (12 * 5 seconds)

	    System.out.println("Please send a message to the bot in Telegram to start...");

	    while (chatId == null && attempts < maxAttempts) {
	        chatId = getChatId(BOT_TOKEN);
	        if (chatId == null) {
	            System.out.println("Waiting for a message... Please write something to the bot.");
	            try {
	                Thread.sleep(5000); // Wait 5 seconds before checking again
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            attempts++;
	        }
	    }

	    if (chatId == null) {
	        System.out.println("Timeout: No message received from the user.");
	        return null;
	    }

	    System.out.println("Chat detected! Chat ID: " + chatId);
	    return chatId;
	}
	
	
	private static Long getChatId(String BOT_TOKEN) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://api.telegram.org")
                .path("/bot" + BOT_TOKEN + "/getUpdates");

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        String jsonResponse = response.readEntity(String.class);
        response.close();
        client.close();

        // Debugging: Print the API response
        System.out.println("API Response: " + jsonResponse);

        // Extract chat_id
        Pattern pattern = Pattern.compile("\"chat\":\\{\"id\":(\\d+),");
        Matcher matcher = pattern.matcher(jsonResponse);

        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }

        return null; // No chat ID found
    }
	

}