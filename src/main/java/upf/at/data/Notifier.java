package upf.at.data;

public class Notifier {
	private String phone;
	private String city;
	private Data data;
	
	
	public Notifier() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public Notifier(String phone, String city, Data data) {
		super();
		this.phone = phone;
		this.city = city;
		this.data = data;
	}



	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}
	

	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}

	
	public Data getData() {
		return data;
	}


	public void setData(Data data) {
		this.data = data;
	}


	public static class Data {
        private int aqi;

        public int getAqi() {
            return aqi;
        }

        
        public void setAqi(int aqi) {
            this.aqi = aqi;
        }
     }
}
