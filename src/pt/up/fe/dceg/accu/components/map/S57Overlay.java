package pt.up.fe.dceg.accu.components.map;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class S57Overlay extends Overlay {
	private static final String TAG = "S57 Overlay";
	private float requestLat1;
	private float requestLon1;
	private float requestLat2;
	private float requestLon2;

	private boolean isMoving = false;
	private Bitmap currentBitmap;

	public S57Overlay() {

	}

	public Bitmap requestBitmap(MapView mapv) {
		Bitmap bitmap = null;
		HttpClient httpclient = new DefaultHttpClient();
		System.out
				.println("http://localhost:8082/map/s57/png?"
						+ "q="
						+ requestLat1
						+ ","
						+ requestLon1
						+ ","
						+ requestLat2
						+ ","
						+ requestLon2
						+ ","
						+ mapv.getWidth()
						+ ","
						+ mapv.getHeight()
						+ "&cs=DAY&dc=All&dsp=false&dsa=true&sf=true&ss=true&ssw=10.0&svsw=5.0&svdw=20.0");

		HttpGet httpget = new HttpGet(
				"http://192.168.106.27:8082/map/s57/png?"
						+ "q="
						+ requestLat1
						+ ","
						+ requestLon1
						+ ","
						+ requestLat2
						+ ","
						+ requestLon2
						+ ","
						+ mapv.getWidth()
						+ ","
						+ mapv.getHeight()
						+ "&cs=DAY&dc=All&dsp=false&dsa=true&sf=true&ss=true&ssw=10.0&svsw=5.0&svdw=20.0");

		HttpResponse response;
		HttpEntity entity = null;
		try {
			response = httpclient.execute(httpget);
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
				is = entity.getContent();
				BitmapDrawable bitmapDrawable = new BitmapDrawable(is);
				bitmap = bitmapDrawable.getBitmap();
			} catch (RuntimeException ex) {

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

	@Override
	public void draw(Canvas canvas, MapView mapv, boolean shadow) {
		Log.i(TAG, "Drawing...");

		if (!isMoving) {
			Log.i(TAG, "Drawing when stopped");

			Projection proj = mapv.getProjection();
			GeoPoint upperLeft = proj.fromPixels(0, 0);
			GeoPoint lowerRight = proj.fromPixels(mapv.getWidth(),
					mapv.getHeight());

			requestLat1 = upperLeft.getLatitudeE6() / 1000000f;
			requestLon1 = upperLeft.getLongitudeE6() / 1000000f;

			requestLat2 = lowerRight.getLatitudeE6() / 1000000f;
			requestLon2 = lowerRight.getLongitudeE6() / 1000000f;

			currentBitmap = requestBitmap(mapv);
		}

		if (currentBitmap != null)
			canvas.drawBitmap(currentBitmap, 0, 0, new Paint());
	}

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE)
			isMoving = true;
		if (e.getAction() == MotionEvent.ACTION_UP)
			isMoving = false;
		return super.onTouchEvent(e, mapView);
	}

}
