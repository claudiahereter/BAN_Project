package upf.at.services;

import java.util.ArrayList;


import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import upf.at.data.Data;
import upf.at.data.Station;

@Path("/stations")
public class StationService {
	private static List<Station> stations = new ArrayList<Station>();  
	
	private long lastUpdated = 0;
    private static final long CACHE_EXPIRATION = TimeUnit.SECONDS.toMillis(120);

	@GET  
    @Path("/list")  
    @Produces(MediaType.APPLICATION_JSON)  
    public List<Station> get() {   
		
		long currentTime = System.currentTimeMillis();
		
		if (stations == null || (currentTime - lastUpdated) > CACHE_EXPIRATION) {
            stations = fetchStations();
            lastUpdated = currentTime;
		}
		
        return stations;  
    }   
	
////// AUXILIAR FUNCION FOR LISTING THE STATIONS	
	public static List<Station> fetchStations(){
		Client client = ClientBuilder.newClient(); 
		 
		WebTarget target = client.target("https://opendata-ajuntament.barcelona.cat")
	                .path("data/dataset/6aa3416d-ce1a-494d-861b7bd07f069600/resource/1b215493-9e63-4a12-8980-2d7e0fa19f85/download");

        String token = "9f0262eca9362338c19546b1830d4855b40e0f2927c74a7927524246889acd4c"; 

        Data data = target.request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", token)
                .get(new GenericType<Data>() {});
        
        return data.getData().getStations();
	}
	
	public static Station getStationById(int stationId){
		for(Station station: stations) {
			if(station.getStation_id() == stationId) {
				return station;
			}
		}
		return null;
	}
	
//////FUNCTION FOR RETRIEVING THE STATIONS LIST	
	public static List<Station> getStations(){
		return stations;
	}
	
}
