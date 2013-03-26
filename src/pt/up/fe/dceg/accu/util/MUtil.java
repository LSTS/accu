package pt.up.fe.dceg.accu.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Class that gathers miscellaneous utilitary functions that don't really fit anywhere
 * If I have a bunch of them in the same category i move them to a separate class
 * @author jqcorreia
 *
 */
public class MUtil {

	private static final String TAG = null;

	/**
	 * Function that rounds a number to N decimal places
	 * @param d Double to round
	 * @param n Number of decimal places to round to
	 * @return
	 */
	public static double roundn(double d, int n)
	{
		d = d * Math.pow(10,n);
		d = Math.round(d);
		d = d / Math.pow(10,n);
		return d;
	}
	/**
	 * Android function to retrieve the IP independent of the connection used (3G or WiFi)
	 * @return string containing the IP
	 */
	public static String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("LocalIpGetter", ex.toString());
	    }
	    return null;
	}
	
	public static String getBroadcastAddress(Context mContext) throws IOException {
	    WifiManager wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    // handle null somehow

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads).getHostAddress().toString();
	}
	
	public static Bitmap requestBitmap(double requestLat1, double requestLon1, 
			double requestLat2, double requestLon2, int w, int h) {
		Bitmap bitmap = null;
		HttpClient httpclient = new DefaultHttpClient();
		String request ="http://192.168.106.27:8082/map/s57/png?"
			+ "q="
			+ requestLat1
			+ ","
			+ requestLon1
			+ ","
			+ requestLat2
			+ ","
			+ requestLon2
			+ ","
			+ w
			+ ","
			+ h
			+ "&cs=DAY&dc=All&dsp=false&dsa=true&sf=true&ss=true&ssw=10.0&svsw=5.0&svdw=20.0";
 
		System.out.println(request);

		HttpGet httpget = new HttpGet(request);

		HttpResponse response;
		HttpEntity entity = null;
		try {
			response = httpclient.execute(httpget);
			System.out.println("RP = " + response.getStatusLine().getReasonPhrase());
			System.out.println(response.getStatusLine().getStatusCode()); 
			entity = response.getEntity();
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (entity != null) {
			InputStream is = null;
			try {
				Log.i(TAG,"Getting Image Content");
				is = entity.getContent();
				Log.i(TAG,"Creating Drawable");
				BitmapDrawable bitmapDrawable = new BitmapDrawable(is);
				Log.i(TAG,"Fetching bitmap data");
				bitmap = bitmapDrawable.getBitmap();
				System.out.println("W = " + bitmap.getWidth() + " H = " + bitmap.getHeight());
			} catch (RuntimeException ex) 
			{
				ex.printStackTrace();
				httpget.abort();
				throw ex;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			httpclient.getConnectionManager().shutdown();
		}
		return bitmap;
	}
}
