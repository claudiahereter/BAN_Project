package upf.at.data;

public class Data {
	private static Stations data;

	public Data(Stations data) {
		super();
		Data.data = data;
	}

	public Data() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	public Stations getData() {
		return data;
	}
	

	public void setData(Stations data) {
		Data.data = data;
	}
	

	@Override
	public String toString() {
		return "Data [data=" + data + ", getData()=" + getData() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	
	
}
