/*
 * Copyright (c) 2004-2011 Laboratório de Sistemas e Tecnologia Subaquática and Authors
 * All rights reserved.
 * Faculdade de Engenharia da Universidade do Porto
 * Departamento de Engenharia Electrotécnica e de Computadores
 * Rua Dr. Roberto Frias, 4200-465 Porto, Portugal
 *
 * For more information please see <http://whale.fe.up.pt/neptus>.
 *
 * Created by 
 * 20??/??/??
 * $Id:: UTMCoordinates.java 4696 2011-01-23 19:20:18Z pdias              $:
 */
package pt.up.fe.dceg.accu.util;

/**
 * 
 * @author ZP
 *
 */
public class UTMCoordinates {
	private final double WGS84_radius = 6378137.0d; 
	private final double WGS84_eccSqared = 0.00669438d;
	
	private double easting = 0.0;
	private double northing = 0.0;

	private double latitude = 0.0;
	private double longitude = 0.0;
	
	private int zone_number = 0;
	private char zone_letter = 'T';
	
	public UTMCoordinates(double easting, double northing, int zone_number, char zone_letter) {
		this.northing = northing;
		this.easting = easting;
		this.zone_number = zone_number;
		this.zone_letter = zone_letter;
		UTMtoLL();
	}
	
	public UTMCoordinates(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		LLtoUTM();		
		//System.out.println(latitude);
		//System.out.println(longitude);
	}
	
	public void UTMtoLL() {
        //check the ZoneNummber is valid
        if (zone_number < 0 || zone_number > 60) {
            return;
        }

        double k0 = 0.9996;
        double a = WGS84_radius;
        double eccSquared = WGS84_eccSqared;
        double eccPrimeSquared;
        double e1 = (1 - Math.sqrt(1 - eccSquared))
                / (1 + Math.sqrt(1 - eccSquared));
        double N1, T1, C1, R1, D, M;
        double LongOrigin;
        double mu, phi1Rad;

        // remove 500,000 meter offset for longitude
        double x = easting/*+500000.0*/ ; 
        double y = northing;

        //We must know somehow if we are in the Northern or Southern
        //hemisphere, this is the only time we use the letter So even
        //if the Zone letter isn't exactly correct it should indicate
        //the hemisphere correctly
        if (zone_letter == 'S') {
            y -= 10000000.0d;//remove 10,000,000 meter offset used
                             // for southern hemisphere
        }

        //There are 60 zones with zone 1 being at West -180 to -174
        
        LongOrigin = (zone_number - 1) * 6 - 180 + 3; //+3 puts origin
                                                     // in middle of
                                                     // zone

        eccPrimeSquared = (eccSquared) / (1 - eccSquared);

        M = y / k0;
        mu = M
                / (a * (1 - eccSquared / 4 - 3 * eccSquared * eccSquared / 64 - 5
                        * eccSquared * eccSquared * eccSquared / 256));

        phi1Rad = mu + (3 * e1 / 2 - 27 * e1 * e1 * e1 / 32) * Math.sin(2 * mu)
                + (21 * e1 * e1 / 16 - 55 * e1 * e1 * e1 * e1 / 32)
                * Math.sin(4 * mu) + (151 * e1 * e1 * e1 / 96)
                * Math.sin(6 * mu);
//        double phi1 = ProjMath.radToDeg(phi1Rad);

        N1 = a
                / Math.sqrt(1 - eccSquared * Math.sin(phi1Rad)
                        * Math.sin(phi1Rad));
        T1 = Math.tan(phi1Rad) * Math.tan(phi1Rad);
        C1 = eccPrimeSquared * Math.cos(phi1Rad) * Math.cos(phi1Rad);
        R1 = a
                * (1 - eccSquared)
                / Math.pow(1 - eccSquared * Math.sin(phi1Rad)
                        * Math.sin(phi1Rad), 1.5);
        D = x / (N1 * k0);

        double Lat = phi1Rad
                - (N1 * Math.tan(phi1Rad) / R1)
                * (D
                        * D
                        / 2
                        - (5 + 3 * T1 + 10 * C1 - 4 * C1 * C1 - 9 * eccPrimeSquared)
                        * D * D * D * D / 24 + (61 + 90 * T1 + 298 * C1 + 45
                        * T1 * T1 - 252 * eccPrimeSquared - 3 * C1 * C1)
                        * D * D * D * D * D * D / 720);
        latitude = Math.toDegrees(Lat);

        double Long = (D - (1 + 2 * T1 + C1) * D * D * D / 6 + (5 - 2 * C1 + 28
                * T1 - 3 * C1 * C1 + 8 * eccPrimeSquared + 24 * T1 * T1)
                * D * D * D * D * D / 120)
                / Math.cos(phi1Rad);
        longitude = LongOrigin + Math.toDegrees(Long);
    }
	
	public void LLtoUTM() {
		double Lat = latitude;
        double Long = longitude;
        
        if (Long <0)
        	Long = 180+Long;
        
        double a = WGS84_radius;
        double eccSquared = WGS84_eccSqared;
        double k0 = 0.9996;

        double LongOrigin;
        double eccPrimeSquared;
        double N, T, C, A, M;

        double LatRad = Math.toRadians(Lat);
        double LongRad = Math.toRadians(Long);
        double LongOriginRad;
        int ZoneNumber;

        ZoneNumber = (int) ((Long + 180) / 6) + 1;

        //Make sure the longitude 180.00 is in Zone 60
        if (Long == 180) {
            ZoneNumber = 60;
        }

        // Special zone for Norway
        if (Lat >= 56.0f && Lat < 64.0f && Long >= 3.0f && Long < 12.0f) {
            ZoneNumber = 32;
        }

        // Special zones for Svalbard
        if (Lat >= 72.0f && Lat < 84.0f) {
            if (Long >= 0.0f && Long < 9.0f)
                ZoneNumber = 31;
            else if (Long >= 9.0f && Long < 21.0f)
                ZoneNumber = 33;
            else if (Long >= 21.0f && Long < 33.0f)
                ZoneNumber = 35;
            else if (Long >= 33.0f && Long < 42.0f)
                ZoneNumber = 37;
        }
        
        
        LongOrigin = (ZoneNumber - 1) * 6 - 180 + 3; //+3 puts origin
                                                     // in middle of
                                                     // zone
        LongOriginRad = Math.toRadians(LongOrigin);

        eccPrimeSquared = (eccSquared) / (1 - eccSquared);

        N = a / Math.sqrt(1 - eccSquared * Math.sin(LatRad) * Math.sin(LatRad));
        T = Math.tan(LatRad) * Math.tan(LatRad);
        C = eccPrimeSquared * Math.cos(LatRad) * Math.cos(LatRad);
        A = Math.cos(LatRad) * (LongRad - LongOriginRad);

        M = a
                * ((1 - eccSquared / 4 - 3 * eccSquared * eccSquared / 64 - 5
                        * eccSquared * eccSquared * eccSquared / 256)
                        * LatRad
                        - (3 * eccSquared / 8 + 3 * eccSquared * eccSquared
                                / 32 + 45 * eccSquared * eccSquared
                                * eccSquared / 1024)
                        * Math.sin(2 * LatRad)
                        + (15 * eccSquared * eccSquared / 256 + 45 * eccSquared
                                * eccSquared * eccSquared / 1024)
                        * Math.sin(4 * LatRad) - (35 * eccSquared * eccSquared
                        * eccSquared / 3072)
                        * Math.sin(6 * LatRad));

        double UTMEasting = (k0
                * N
                * (A + (1 - T + C) * A * A * A / 6.0d + (5 - 18 * T + T * T
                        + 72 * C - 58 * eccPrimeSquared)
                        * A * A * A * A * A / 120.0d)/* + 500000.0d*/);

        double UTMNorthing =  (k0 * (M + N
                * Math.tan(LatRad)
                * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A
                        / 24.0d + (61 - 58 * T + T * T + 600 * C - 330 * eccPrimeSquared)
                        * A * A * A * A * A * A / 720.0d)));
        if (Lat < 0.0f) {
            UTMNorthing += 10000000.0f; //10000000 meter offset for
                                        // southern hemisphere
        }

        northing = UTMNorthing;
        easting = UTMEasting;
        
        //System.out.println("Easting: "+easting+ " Northing: "+northing);
        
        zone_number = ZoneNumber;
        zone_letter = 'N';
        if (Lat < 0)
        	zone_letter = 'S';
	}

	public double getEasting() {
		return easting;
	}

	public void setEasting(double easting) {
		this.easting = easting;
		UTMtoLL();
	}

	public double getNorthing() {
		return northing;
	}

	public void setNorthing(double northing) {
		this.northing = northing;
		UTMtoLL();
	}

	public char getZone_letter() {
		return zone_letter;
	}

	public int getZone_number() {
		return zone_number;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
		LLtoUTM();
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
		LLtoUTM();
	}	 
	
	/*
	public final static double radToDeg(double rad) {
        return (rad * (180.0d / Math.PI));
    }
    */

}
