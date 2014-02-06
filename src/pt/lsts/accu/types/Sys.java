package pt.lsts.accu.types;

public class Sys {
	
	private String mAddress;
	private int mPort;
	private String mName;
	private int mId;
	public long lastMessageReceived;
	private String mType;
	private double[] LLD={0.0,0.0,0.0}; // Lat Lon depth in radians and meters
	private double[] NED={0.0,0.0,0.0}; // North East Down in meters
	private double[] RPY={0.0,0.0,0.0}; // Roll Pitch Yaw in radians
	
	private String refMode; // Reference Mode name
	
	// This 2 Booleans are used to compute the color of 
	// each sys in system list and serve as the actual state
	boolean mConnected;
	boolean mError;
	
	
	public boolean isError() {
		return mError;
	}

	public void setError(boolean error) {
		this.mError = error;
	}

	public boolean isConnected() {
		return mConnected;
	}

	public void setConnected(boolean mConnected) {
		this.mConnected = mConnected;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String address) {
		this.mAddress = address;
	}

	public int getPort() {
		return mPort;
	}

	public void setPort(int port) {
		this.mPort = port;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}
	
	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}
	public String getType()
	{
		return mType;
	}
	public double[] getLLD() {
		return LLD;
	}

	public void setLLD(double[] lLD) {
		LLD = lLD;
	}

	public double[] getNED() {
		return NED;
	}

	public void setNED(double[] nED) {
		NED = nED;
	}

	public double[] getRPY() {
		return RPY;
	}

	public void setRPY(double[] rPY) {
		RPY = rPY;
	}
	public String getRefMode() {
		return refMode;
	}

	public void setRefMode(String refMode) {
		this.refMode = refMode;
	}
	public Sys(String address, int port, String name, int id, String type, boolean connected, boolean error) {
		super();
		this.mAddress = address;
		this.mPort = port;
		this.mName = name;
		this.mId = id;
		this.mConnected = connected;
		this.mError = error;
		this.mType = type;
		lastMessageReceived = System.currentTimeMillis();
	}
	
}
