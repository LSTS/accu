package pt.up.fe.dceg.accu;

import pt.up.fe.dceg.accu.msg.IMCSubscriber;
import pt.up.fe.dceg.accu.panel.AccuAction;
import pt.up.fe.dceg.accu.panel.AccuBasePanel;
import pt.up.fe.dceg.accu.state.Accu;
import pt.up.fe.dceg.accu.util.AccuTimer;
import pt.up.fe.dceg.neptus.imc.IMCMessage;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

@AccuAction(name = "Text2Speech (wip)", icon=R.drawable.icon)
public class TtsTestPanel extends AccuBasePanel implements OnClickListener, IMCSubscriber, OnEditorActionListener
{
	TextToSpeech tts;
	private int currentSpeed;
	EditText et;
	
	AccuTimer timer = new AccuTimer(new Runnable(){

		@Override
		public void run() {
			System.out.println(currentSpeed);
			tts.speak(String.valueOf(currentSpeed), 0, null);
		}
		
	}, 5000);
	
	public TtsTestPanel(Context context)
	{
		super(context);
		et = (EditText) getLayout().findViewWithTag("text");
		et.setOnEditorActionListener(this);
	}

	@Override
	public void onStart() 
	{
		tts = new TextToSpeech(getContext(), new OnInitListener() {
			
			@Override
			public void onInit(int status) {
				
			}
		});
		timer.start();
		Button btn = (Button) getLayout().findViewWithTag("btn");
		
		btn.setOnClickListener(this);
		
		Accu.getInstance().getIMCManager().addSubscriber(this, "EstimatedState");
	}

	@Override
	public void onStop() 
	{
		timer.stop();
		tts.shutdown();
		Accu.getInstance().getIMCManager().removeSubscriberToAll(this);
	}

	@Override
	public View buildLayout() {
		return inflateFromResource(R.layout.ttstest_layout);
	}

	@Override
	public int getIcon() {
		return R.drawable.icon;
	}

	@Override
	public void onClick(View arg0) 
	{
		String str = ((EditText)getLayout().findViewWithTag("text")).getText().toString();
		tts.speak(str, 0, null);
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

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		try{
			timer.setDelay(Integer.parseInt(arg0.getText()+"")*1000);
		}
		catch( NumberFormatException e)
		{
			System.out.println("ganha juizo");
		}
		return true;
	}

}
