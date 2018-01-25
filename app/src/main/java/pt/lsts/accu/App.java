package pt.lsts.accu;

import android.app.Application;

/**
 * Class extending application that does a single startup for the application needed to initialize
 * ACCU state object
 * @author sharp
 *
 */
public class App extends Application 
{	
	@Override
	public void onCreate()
	{
		super.onCreate();
   	}
}
