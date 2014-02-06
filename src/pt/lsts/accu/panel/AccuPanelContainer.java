package pt.lsts.accu.panel;

import java.util.ArrayList;

import pt.up.fe.dceg.accu.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class AccuPanelContainer extends LinearLayout
implements PanelItemClickListener
{
	private static final String TAG = "AccuPanelContainer";
	ArrayList<AccuBaseAction> actionList = new ArrayList<AccuBaseAction>();
	FrameLayout mainFrame;
	ActionSelector selector;
	public int currentPanelId;
	AccuBasePanel currentPanel;
	
	public AccuPanelContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.container_layout,this,true);
		mainFrame = (FrameLayout)findViewById(R.id.frame);
		
		selector = (ActionSelector) findViewById(R.id.selector);
		selector.setList(actionList);
		selector.setPanelChangeListener(this);
//		setKeepScreenOn(true);
	}
	public void addAction(AccuBaseAction action)
	{
		actionList.add(action);
	}
	
	public void openPanel(int id)
	{
		stopCurrentPanel();
		mainFrame.removeAllViews();
		mainFrame.addView(((AccuBasePanel)actionList.get(id)).getLayout());
		startPanelWithId(id);
		currentPanelId = id;
		currentPanel = (AccuBasePanel)actionList.get(id);
		selector.closeSelector();
	}
	
	public void startPanelWithId(int id)
	{
		((AccuBasePanel)actionList.get(id)).startPanel();
	}
	
	public void stopCurrentPanel()
	{
		stopPanel(currentPanel);
	}
	public void stopPanelWithId(int id)
	{
		if(actionList.get(id) instanceof AccuBasePanel)
			((AccuBasePanel)actionList.get(id)).stopPanel();
	}
	public void stopPanel(AccuBasePanel panel)
	{
		if(panel!=null)
			panel.stopPanel();
		else
			Log.e(TAG,"Error stopping panel");
	}
	public void doAction(int id)
	{
		if(actionList.get(id) instanceof AccuBasePanel)
		{
			openPanel(id);
		}
		if(actionList.get(id) instanceof AccuBaseCommand)
		{
			((AccuBaseCommand)actionList.get(id)).command();
		}
	}
	
	public ActionSelector getSelector()
	{
		return selector;
	}
	
	public void clear()
	{
		if(actionList.size()!=0)
		{
//			stopCurrentPanel();
			actionList.clear();
		}
	}
	@Override
	public void onPanelItemClick(int newPanelId) {
		doAction(newPanelId);
	}
	public AccuBasePanel getPanelWithId(int id)
	{
		return (AccuBasePanel) actionList.get(id);
	}
	public AccuBasePanel getCurrentPanel()
	{
		return getPanelWithId(currentPanelId);
	}
}
