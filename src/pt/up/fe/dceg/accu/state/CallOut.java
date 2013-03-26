package pt.up.fe.dceg.accu.state;

import pt.up.fe.dceg.accu.msg.IMCSubscriber;
import pt.up.fe.dceg.accu.util.AccuTimer;
import pt.up.fe.dceg.neptus.imc.IMCMessage;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class CallOut implements IMCSubscriber{
	TextToSpeech tts;
	private int currentSpeed;
	AccuTimer timer = new AccuTimer(new Runnable(){

		@Override
		public void run() {
			System.out.println(currentSpeed);
			tts.speak(String.valueOf(currentSpeed), 0, null);
		}
		
	}, 5000);
	private Context context;
	private boolean started=false;
	
	public CallOut(Context context)
	{
		this.context = context;
		setDelay(2000);
	}
	@Override
	public void onReceive(IMCMessage msg) 
	{
		if(msg.getAbbrev().equalsIgnoreCase("EstimatedState"))
		{
			double vx = msg.getDouble("vx");
			double vy = msg.getDouble("vy");
			double vz = msg.getDouble("vz");
			double speed = Math.sqrt(Math.pow(vx, 2)+Math.pow(vy, 2)+Math.pow(vz, 2));
			currentSpeed = (int)speed;
		}
	}
	
	public void start()
	{
		tts = new TextToSpeech(context, new OnInitListener() {
			
			@Override
			public void onInit(int status) {
				
			}
		});
		timer.start();
		Accu.getInstance().getIMCManager().addSubscriber(this, "EstimatedState");
		started = true;
	}
	
	public void stop()
	{
		timer.stop();
		tts.shutdown();
		Accu.getInstance().getIMCManager().removeSubscriberToAll(this);
		started = false;
	}
	
	public void toggle()
	{
		if(started) stop();
		else start();
			
	}
	public void setDelay(int delaymillis)
	{
		timer.setDelay(delaymillis);
	}
}
