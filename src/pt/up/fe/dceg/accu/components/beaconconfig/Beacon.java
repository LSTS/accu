package pt.up.fe.dceg.accu.components.beaconconfig;

import pt.up.fe.dceg.accu.util.CoordUtil;

public class Beacon {

	String name;
	double lat;
	double lon;
	double depth;
	int interrogationChannel;
	int replayChannel;
	int transponderDelay;
	
	public Beacon(String name, double lat, double lon, double depth) {
		super();
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.depth = depth;
	}
	public String getName()
	{
		return name;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getDepth() {
		return depth;
	}
	public void setDepth(double depth) {
		this.depth = depth;
	}
	public int getInterrogationChannel() {
		return interrogationChannel;
	}
	public void setInterrogationChannel(int interrogationChannel) {
		this.interrogationChannel = interrogationChannel;
	}
	public int getReplayChannel() {
		return replayChannel;
	}
	public void setReplyChannel(int replayChannel) {
		this.replayChannel = replayChannel;
	}
	public int getTransponderDelay() {
		return transponderDelay;
	}
	public void setTransponderDelay(int transponderDelay) {
		this.transponderDelay = transponderDelay;
	}
	
	public String getLatDMS()
	{
		return CoordUtil.degreesToDMS(lat, true);
	}
	public String getLonDMS()
	{
		return CoordUtil.degreesToDMS(lon, false);	
	}
	@Override
	public String toString()
	{
		return "lat: " + lat + " lon: " + lon + " replayChannel: " + replayChannel
				+ " interrogationChannel: " + interrogationChannel;
	}
}
