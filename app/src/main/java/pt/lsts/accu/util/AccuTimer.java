package pt.lsts.accu.util;

import android.os.Handler;
import android.os.Message;

/**
 * Helper class that implements a regular simple Timer for one runnable
 * @author sharp
 *
 */
public class AccuTimer {
	long millisecs;
	Runnable runnable;
	boolean running = false;
	
	Handler handle = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(running)
			{
				post(runnable);
				sendMessageDelayed(new Message(), millisecs);
			}
		}
	};
	
	public AccuTimer(Runnable runnable, long delay)
	{
		this.runnable = runnable;
		millisecs = delay;
	}
	
	public void start()
	{
		running = true;
		handle.sendMessageDelayed(new Message(), 0); // Start immediately
	}
	public void stop()
	{
		running = false;
	}
	public void setDelay(long delay)
	{
		millisecs = delay;
	}
}
