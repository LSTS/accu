package pt.up.fe.dceg.accu.panel;

import java.util.ArrayList;

import pt.up.fe.dceg.accu.util.LayoutUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Primitive panel class that supports custom layouts, Panel selector icon and custom Menus
 * Extend this class to create custom panels
 * @author jqcorreia
 *
 */
public abstract class AccuBasePanel extends AccuBaseAction
{
	protected ArrayList<AccuComponent> componentList = new ArrayList<AccuComponent>();
	View layout;
	Context context;
	boolean running;
	
	public AccuBasePanel(Context context)
	{
		this.context = context;
		getLayout(); // For now initialize all components upfront
		populateComponentList();
		running = false;
	}
	
	
	public void populateComponentList()
	{
		ArrayList<View> list = LayoutUtil.flattenLayout(layout);
		for(View v : list)
		{
			if(v instanceof AccuComponent)
			{
				componentList.add((AccuComponent)v);
			}
		}
	}
	public Context getContext()
	{
		return context;
	}
	public View getLayout()
	{
		if(layout==null)
		{
			layout = buildLayout();
		}
		return layout;
	}
	
	public void startPanel()
	{
		running = true;
		startComponents();
		onStart();
	}
	public void stopPanel()
	{
		running = false;
		stopComponents();
		onStop();
	}
	private void startComponents()
	{
		for(AccuComponent c : componentList)
			c.onStart();
	}
	private void stopComponents()
	{
		for(AccuComponent c : componentList)
			c.onEnd();
	}
	protected ViewGroup inflateFromResource(int id)
	{
		return (ViewGroup) LayoutInflater.from(getContext()).inflate(id,null,true);
	}
	public int getType()
	{
		return TYPE_PANEL;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	/**
	 * Override in child class to setup Menu
	 * @param menu the Menu instance to be populated
	 */
	public void prepareMenu(Menu menu)
	{
		// call clear() everytime so one Panel menu doesn't pass to the other
		// Not that elegant but it works...
		menu.clear(); 
	}
	/**
	 * Override in child class to handle menu item selections
	 * @param item Selected Item
	 */
	public void menuHandler(MenuItem item)
	{
		
	}
	
	//Abstract Methods
	public abstract void onStart();
	public abstract void onStop();
	public abstract View buildLayout();



}
