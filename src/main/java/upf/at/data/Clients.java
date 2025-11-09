package upf.at.data;

import java.util.List;

public class Clients {
	private String phone;
	private String telegramToken;
	private List<Integer> stationsIds;
	
	public Clients() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Clients(String phone, String telegramToken, List<Integer> stationsIds) {
		super();
		this.phone = phone;
		this.telegramToken = telegramToken;
		this.stationsIds = stationsIds;
	}
	

	public String getPhone() {
		return phone;
	}
	

	public void setPhone(String phone) {
		this.phone = phone;
	}
	

	public String getTelegramToken() {
		return telegramToken;
	}

	
	public void setTelegramToken(String telegramToken) {
		this.telegramToken = telegramToken;
	}
	

	public List<Integer> getStationsIds() {
		return stationsIds;
	}
	

	public void setStationsIds(List<Integer> stationsIds) {
		this.stationsIds = stationsIds;
	}

	
	@Override
	public String toString() {
		return "Client [phone=" + phone + ", telegramToken=" + telegramToken + "]";
	}
	
	
}
