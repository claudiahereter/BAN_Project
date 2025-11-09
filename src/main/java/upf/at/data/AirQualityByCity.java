package upf.at.data;

public class AirQualityByCity {
	private String city;
	private String phone;
	
	
	public AirQualityByCity() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	public AirQualityByCity(String phone, String city) {
		super();
		this.phone = phone;
		this.city = city;
	}

	
	public String getPhone() {
		return phone;
	}

	
	public void setPhone(String phone) {
		this.phone = phone;
	}

	
	public String getCity() {
		return city;
	}
	

	public void setCity(String city) {
		this.city = city;
	}

	
	@Override
	public String toString() {
		return "AirQualityByCity [phone=" + phone + ", city=" + city + "]";
	}
	
	
}
