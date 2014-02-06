package pt.lsts.accu.components;

import java.util.TimerTask;

import pt.lsts.accu.adapter.EntityListAdapter;
import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.msg.IMCSubscriber;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.state.MainSysChangeListener;
import pt.lsts.accu.types.Sys;
import pt.lsts.accu.util.AccuTimer;
import pt.lsts.imc.IMCMessage;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

public class EntityStateList extends ListView 
implements IMCSubscriber, MainSysChangeListener 
{
	public static final String[] SUBSCRIBED_MSGS = {"EntityList","EntityState"};
	
	Context mContext;
	private EntityListAdapter mAdapter;
	boolean alreadyReceived;
	AccuTimer timer;	
	IMCManager imm;
	
	Runnable task = new Runnable()
	{
		@Override
		public void run() {
			if(imm.getListener()==null) return;
			imm.sendToActiveSys("EntityList", "op",1);
		}	
	};
	
	public EntityStateList(Context context) {
		super(context);
		this.mContext = context;
		initialize(context);
	}
	public EntityStateList(Context context, AttributeSet attribSet) {
		super(context,attribSet);
		this.mContext = context;
		initialize(context);
	}
	public void initialize(Context ct)
	{
		imm = Accu.getInstance().getIMCManager();
		imm.addSubscriber(this, SUBSCRIBED_MSGS);
		Accu.getInstance().addMainSysChangeListener(this);
		timer = new AccuTimer(task, 5000);
		alreadyReceived = false;
	}

	@Override
	public void onReceive(IMCMessage msg) 
	{	
		//FIXME
		// If active system doesn't exist or isn't a message from active system
		if(Accu.getInstance().getActiveSys()==null)
			return;
		if(Accu.getInstance().getActiveSys().getId() != (Integer)msg.getHeaderValue("src"))
			return;
		
		if(msg.getAbbrev().equalsIgnoreCase("EntityList"))
		{
			if(!alreadyReceived)
			{
				mAdapter = new EntityListAdapter(msg,mContext);
				setAdapter(mAdapter);
				alreadyReceived = true;
				timer.stop();
			}
		}
		
		if(msg.getAbbrev().equalsIgnoreCase("EntityState"))
		{
			if(mAdapter!=null)
			{
				mAdapter.updateState(msg);
			}
		}
	}
	@Override
	public void onMainSysChange(Sys newMainSys) {
		Log.i("ESL Logging",newMainSys.getName());
		alreadyReceived=false;
		timer.start();
		setAdapter(null);
	}
	
	public TimerTask getTimerTask()
	{
		return new TimerTask() 
		{
			@Override
			public void run() {
				if(imm.getListener()==null) return;
				imm.sendToActiveSys("EntityList", "op",1);
			}		
		};
	}
}
