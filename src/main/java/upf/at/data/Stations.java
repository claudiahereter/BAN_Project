package upf.at.data;

import java.util.List;



public class Stations {
    private List<Station> stations;
    

	public Stations(List<Station> stations) {
		super();
		this.stations = stations;
	}

	
	public Stations() {
		super();
	}

	
	public List<Station> getStations() {
        return stations;
    }

	
	public void setStations(List<Station> stations) {
		this.stations = stations;
	}
	

	@Override
	public String toString() {
		return "Stations [getStations()=" + getStations() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
    
    
  
}