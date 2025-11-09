package upf.at.data;

public class Phone {
    private String phone;
    
    public Phone() {
		super();
		// TODO Auto-generated constructor stub
	}
    

	public Phone(String phone) {
		super();
		this.phone = phone;
	}
	

	// Getter and setter
    public String getPhone() {
        return phone;
    }

    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    
	@Override
	public String toString() {
		return "PhoneRequest [phone=" + phone + "]";
	}
    
    
}