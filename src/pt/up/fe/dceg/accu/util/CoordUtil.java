package pt.up.fe.dceg.accu.util;

import pt.up.fe.dceg.neptus.imc.IMCMessage;

public class CoordUtil {
	/**
	 * Computes the offset (north, east) in meters from
	 * (lat, lon) to (alat, alon) [both of these in decimal degrees].<br>
	 * Subtract the two latlons and come up with the distance
	 * in meters N/S and E/W between them.
	 */
	public static double msToKnot = 1.9438612860586;
	public static double[] latLonDiff(double lat, double lon, double alat,double alon)
	{

		if (lat == alat && lon == alon)
			return new double[] {0,0};
		
		UTMCoordinates coords1 = new UTMCoordinates(lat, lon);
		UTMCoordinates coords2 = new UTMCoordinates(alat, alon);
		
		double diff[] =  new double[2];
		
		diff[0] = coords2.getNorthing() - coords1.getNorthing();
		diff[1] = coords2.getEasting() - coords1.getEasting();
		return diff;
	}
	
	/**
     * Modified by jqcorreia for ACCU
     * @return The total Lat(), Lon() and Depth(m)
     */
    public static double[] getAbsoluteLatLonDepth(double latitude, double longitude, double depth,
    										double offsetNorth, double offsetEast, double offsetDown)
    {
    	
        double[] totalLatLonDepth = new double[]{0d, 0d, 0d};
        
        totalLatLonDepth[0] = latitude;
        totalLatLonDepth[1] = longitude;
        totalLatLonDepth[2] = depth;

        //Add Rect. Offsets
        double[] tmpDouble = latLonAddNE2(totalLatLonDepth[0],
                totalLatLonDepth[1], offsetNorth, offsetEast);
        totalLatLonDepth[0] = tmpDouble[0];
        totalLatLonDepth[1] = tmpDouble[1];
        totalLatLonDepth[2] += offsetDown;
        
        //Add Sph. Offsets
//        tmpDouble = CoordinateUtil.sphericalToCartesianCoordinates(
//                getOffsetDistance(), getAzimuth(), getZenith());
//        totalLatLonDepth[2] += tmpDouble[2];
//        tmpDouble = CoordinateUtil.latLonAddNE2(totalLatLonDepth[0],
//                totalLatLonDepth[1], tmpDouble[0], tmpDouble[1]);
//        totalLatLonDepth[0] = tmpDouble[0];
//        totalLatLonDepth[1] = tmpDouble[1];

        return totalLatLonDepth;    	
    }
    /**
     * Returns an Absolute Position based solely on a IMCMessage
     * This message has to be an EstimatedState (or equivalent)
     * @param msg Message to process
     * @return Array of size 3 with Lat-Lon-Depth 
     */
    public static double[] getAbsoluteLatLonDepthFromMsg(IMCMessage msg)
    {
    	String ref = ""+msg.getString("ref");
    	
    	if(ref.equalsIgnoreCase("LLD_ONLY"))
    	{
    		double[] ned = {0.0,0.0,0.0};
    		double[] lld = {msg.getDouble("lat"),msg.getDouble("lon"),msg.getDouble("depth")};
    		return getAbsoluteLatLonDepth(Math.toDegrees(lld[0]), Math.toDegrees(lld[1]), Math.toDegrees(lld[2]), ned[0], ned[1], ned[2]);
    	}
    	if(ref.equalsIgnoreCase("NED_ONLY"))
    	{
    		double[] ned = {msg.getDouble("x"),msg.getDouble("y"),msg.getDouble("z")};
    		double[] lld = {0.0,0.0,0.0};
    		return getAbsoluteLatLonDepth(Math.toDegrees(lld[0]), Math.toDegrees(lld[1]), Math.toDegrees(lld[2]), ned[0], ned[1], ned[2]);

    	}
    	if(ref.equalsIgnoreCase("NED_LLD"))
    	{
    		double[] ned = {msg.getDouble("x"),msg.getDouble("y"),msg.getDouble("z")};
    		double[] lld = {msg.getDouble("lat"),msg.getDouble("lon"),msg.getDouble("depth")};
    		return getAbsoluteLatLonDepth(Math.toDegrees(lld[0]), Math.toDegrees(lld[1]), Math.toDegrees(lld[2]), ned[0], ned[1], ned[2]);
    	}
    	// Workaround to not return null because of nullpointerexception
    	double[] res = {0,0,0};
    	return res;
    }
    
	/**
	 * Add an offset in meters(north, east) to a
	 * (lat,lon) in decimal degrees
	 *  Modified by jqcorreia for ACCU
	 */
	public static double[] latLonAddNE2(double lat, double lon, double north, double east)
	{	
		final double meterToFeet = 3.2808399;
		
		GISCoordinate coord = new GISCoordinate(lat, lon, false);		
		try {
			double angRad = Math.atan2(east, north);
			double dist = Math.sqrt(north*north+east*east);
			
			coord.move(dist*meterToFeet, Math.toDegrees(angRad), GISCoordinate.WGS84);
			
			double rest[] = latLonDiff(lat, lon, coord.getLatInDecDeg(), coord.getLonInDecDeg());
			rest[0] = 0;
			rest[1] = -rest[1]+east;			
			angRad = Math.atan2(rest[1], rest[0]);
			dist = Math.sqrt(rest[0]*rest[0]+rest[1]*rest[1]);
			coord.move(dist*meterToFeet, Math.toDegrees(angRad), GISCoordinate.WGS84);
			
			rest = latLonDiff(lat, lon, coord.getLatInDecDeg(), coord.getLonInDecDeg());
			rest[0] = -rest[0]+north;
			rest[1] = 0;			
			angRad = Math.atan2(rest[1], rest[0]);
			dist = Math.sqrt(rest[0]*rest[0]+rest[1]*rest[1]);
			coord.move(dist*meterToFeet, Math.toDegrees(angRad), GISCoordinate.WGS84);
		}
		catch (Exception e) {

		}
		
		return new double[] {coord.getLatInDecDeg(), coord.getLonInDecDeg()};
	}
	/**
	 * Distance between 2 LatLon points (ignoring depth)
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return the distance between the 2 points in Kilometers
	 */
	public static double dist2LatLon(double lat1, double lon1, double lat2, double lon2)
	{
		int R = 6371; // km
		double dLat = Math.toRadians(lat2-lat1);
		double dLon = Math.toRadians(lon2-lon1); 
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
		        Math.sin(dLon/2) * Math.sin(dLon/2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = R * c;
		
		return d;
	}
	/**
	 * The heading that point 1 needs to follow to get to point 2
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return Bearing in degress (0 to 360 degrees)
	 */
	public static double bearing2LatLon(double lat1, double lon1, double lat2, double lon2)
	{
		double dLon = Math.toRadians(lon2-lon1);
		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);
		
		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1)*Math.sin(lat2) -
		        Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
		double bearing = Math.toDegrees(Math.atan2(y, x));
		
		return (bearing+360)%360;
	}
	/**
	 * 
	 * @param deg Degrees to convert
	 * @param isLat boolean to check if it is a latitude, if false the calculation is done assuming longitude
	 * @return String expressing the conversion in the DºM'S" format
	 */
	public static String degreesToDMS(double deg, boolean isLat)
	{
		String dir="";
		
		if(isLat)
			dir =  (deg < 0? "S":"N");
		else
			dir =  (deg < 0? "W":"E");
		
		deg = Math.abs(deg);
		
		double min = (deg-((int)deg))*60;
		double sec = (min-((int)min))*60;
		
		deg = (int)deg;
		min = (int)min;
		sec = (int)sec;
		
		if(sec==60)
		{
			sec = 0;
			min++;
		}
		if(min==60)
		{
			min=0;
			deg++;
		}
		
		return dir + (int)deg + "°"+(int)min+"'"+(int)sec+"\"";	
	}
	
	
}
