package pt.lsts.accu.components.map;

public class PCCWaypoint {

	double lat;
	double lon;
	int indexNext;
	float lradius;
	int index;
	public PCCWaypoint(double lat, double lon, int index, int indexNext, float lradius)
	{
		this.lat = Math.toDegrees(lat)*1000000;
		this.lon = Math.toDegrees(lon)*1000000;
		this.indexNext= indexNext;
		this.lradius=lradius;
		this.index = index;
	}
}
