package upf.at.data;

public class AirQuality {
	private String phone;
	private String ipAddress;
	
	
	public AirQuality() {
		super();
		// TODO Auto-generated constructor stub
	}


	public AirQuality(String phone, String ipAddress) {
		super();
		this.phone = phone;
		this.ipAddress = ipAddress;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getIpAddress() {
		return ipAddress;
	}


	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	@Override
	public String toString() {
		return "AirQuality [phone=" + phone + ", ipAddress=" + ipAddress + "]";
	}
	
	
	
}
