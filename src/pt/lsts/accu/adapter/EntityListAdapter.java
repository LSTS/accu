package pt.lsts.accu.adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import pt.lsts.accu.state.Accu;
import pt.lsts.accu.types.EntityStateType;
import pt.lsts.imc.IMCMessage;
import pt.lsts.accu.R;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntityListAdapter extends BaseAdapter {

	LinkedHashMap<Integer, String> mEntityList;
	ArrayList<EntityStateType> mEntityStateList;
	Context context;
	
	LinkedHashMap<String,Integer> mColors = new LinkedHashMap<String,Integer>();
	
	public EntityListAdapter(IMCMessage message, Context context)
	{
		System.out.println("Initializing Adapter");
		mEntityList = new LinkedHashMap<Integer,String>();
		mEntityStateList = new ArrayList<EntityStateType>();
		this.context = context;

		// Initialize Colors
		mColors.put("BOOT",Color.BLUE);
		mColors.put("NORMAL",Color.rgb(0, 200, 125));
		mColors.put("FAULT",Color.rgb(200, 200, 0));
		mColors.put("ERROR",Color.rgb(255, 127, 0));
		mColors.put("FAILURE",Color.RED);
		
		
		// Debug
		System.out.println("EntityList of: "+Accu.getInstance().getActiveSys().getName());
		
		// Convert Entity List message into mEntityList <int,String> hashmap
		LinkedHashMap<String, String> elist = message.getTupleList("list");
		for(String key: elist.keySet())
		{
			System.out.println(key + " - " + elist.get(key)+"\n");
			mEntityList.put(Integer.valueOf(elist.get(key)),key); //FIXME
		}
	}
	@Override
	public boolean isEnabled(int position)
	{
		return false;
	}
	@Override
	public int getCount() {
		return mEntityStateList.size();
	}

	@Override
	public EntityStateType getItem(int position) {
		return mEntityStateList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout view = (LinearLayout)convertView; 
		
		if(view == null)
			view = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.entitystate_item_layout, null);
		
		EntityStateType est=getItem(position);
		String strState;
		Log.wtf("EntityStateType: ", est.toString());
		Log.wtf("est.getState(): ", est.getState());
		strState = est.getState() + " " + est.getDescription();
		Log.wtf("strState: ", strState);
		//((TextView)view.findViewWithTag("name")).setTextColor(mColors.get(est.getState()));
		((TextView)view.findViewWithTag("name")).setText(est.getEntity());
		//Log.d("est.getState(): ", est.getState());
		//((TextView)view.findViewWithTag("info")).setTextColor(mColors.get(est.getState()));
		
		((TextView)view.findViewWithTag("info")).setText(strState);

		return view;
	}

	private int posEntity(String entity)
	{
		
		for(int c=0; c < mEntityStateList.size(); c++)
		{
			if(mEntityStateList.get(c).getEntity().equalsIgnoreCase(entity))
				return c;
		}
		return -1;
	}
	
	public void updateState(IMCMessage msg)
	{
		String entity = mEntityList.get((Integer)msg.getHeaderValue("src_ent"));
		String state = msg.getString("state");
		if(entity==null) return; // had to put this here because simulator send bogus EntityState
		int pos = posEntity(entity);
		if(pos >= 0)
		{
			if(mEntityStateList.get(pos).getState().equalsIgnoreCase(state)) // If there isn't any changes 
				return; // Don't update
			
			// UPDATE
			EntityStateType est = new EntityStateType(entity,msg.getString("state"),msg.getString("description"),0);
			mEntityStateList.set(pos, est);
		}
		else
		{
			// ADD
			EntityStateType est = new EntityStateType(entity,msg.getString("state"),msg.getString("description"),0);
			mEntityStateList.add(est);
		}
		notifyDataSetChanged();
	}
}
