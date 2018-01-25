package pt.lsts.accu.components.beaconconfig;

import java.util.ArrayList;

import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.state.BeaconListChangeListener;
import pt.lsts.accu.types.Sys;
import pt.lsts.accu.util.CoordUtil;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.accu.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class BeaconConfigurator 
extends LinearLayout 
implements OnClickListener, BeaconListChangeListener, OnItemClickListener, OnDismissListener, android.content.DialogInterface.OnClickListener
{
	public static final String TAG = "BeaconConfig";

	private static final boolean DEBUG = false;
	
	public Context context;
	ArrayList<Beacon> beaconList = new ArrayList<Beacon>();
	ListView list;
	Button button;
	IMCManager imm = Accu.getInstance().getIMCManager();

	private String[] items;

	private Sys targetSys;

	private View emptyView;
	
	public BeaconConfigurator(Context context) {
		super(context);
		initialize(context);
	}

	public BeaconConfigurator(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	
	public void initialize(final Context context)
	{
		this.context = context;
		
		Accu.getInstance().getLblBeaconList().addBeaconListChangeListener(this);
	
		LayoutInflater.from(context).inflate(R.layout.beacon_config_layout, this,true);
		
		list = (ListView)findViewWithTag("listBeacon");
		emptyView = findViewWithTag("empty");
		
		list.setAdapter(new BaseAdapter(){

			@Override
			public int getCount() 
			{	
				return beaconList.size();
			}

			@Override
			public Object getItem(int position) {
				return (Beacon)beaconList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v= convertView;
				
				if(v == null)
					v = LayoutInflater.from(context).inflate(R.layout.beacon_list_item, null,false);
				
				Beacon beacon = (Beacon)getItem(position);
				TextView tv = (TextView)v.findViewWithTag("lat");
				tv.setText(CoordUtil.degreesToDMS(beacon.getLat(),true)+"");
				tv = (TextView)v.findViewWithTag("lon");
				tv.setText(CoordUtil.degreesToDMS(beacon.getLon(),false)+"");
				tv = (TextView)v.findViewWithTag("id");
				tv.setText(beacon.getName());
				tv = (TextView)v.findViewWithTag("depth");
				tv.setText(beacon.getDepth()+"");
			
				return v;
			}
			
		});
		
		button = ((Button)findViewWithTag("btnSend"));
		button.setOnClickListener(this);
		if(beaconList.size()==0)
		{
			button.setEnabled(false);
			setEmpty(true);
		}
	}
	
	private void setEmpty(boolean b) 
	{
		if(b)
		{
			list.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		}
		else
		{
			list.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
		}
	}

	public void setBeaconList(ArrayList<Beacon> beaconList)
	{
		Log.i(TAG,"Received New Beacon List");
		this.beaconList = beaconList;
		((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
		button.setEnabled(true);
		list.setOnItemClickListener(this);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setEmpty(false);
	}

	@Override
	public void onClick(View view) 
	{
		// Get a CCU list
		ArrayList<String> list =  Accu.getInstance().getSystemList().getNameListByType("CCU");
		items = new String[list.size()];
		list.toArray(items);
		
		// Build an AlertDialog based on this system list
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Select Operational Console:");
		builder.setItems(items, this);
		
		AlertDialog alert = builder.create();
		
		alert.show();
		
		if(DEBUG)
		{
			for (Beacon b : beaconList) 
			{
				System.out.println(b.toString());
			}
		}
	}
	
	private void sendBeaconConfig()
	{
		IMCMessage config;
		
		try {
			config = IMCDefinition.getInstance().create("LblConfig");
			
			for(int i = 0; i < beaconList.size(); i++)
			{
				config.setValue("beacon"+i, getBeaconMessage(beaconList.get(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			config = null;
		}
		if(config!=null)
		{
			System.out.println("Sending");
			imm.sendToSys(targetSys,config);
		}
	}
	
	private IMCMessage getBeaconMessage(Beacon b)
	{
		IMCMessage m;
		try {
				m = IMCDefinition.getInstance().create("LblBeacon", 
				"beacon",b.getName(),
				"lat",Math.toRadians(b.getLat()),
				"lon",Math.toRadians(b.getLon()),
				"depth",b.getDepth(),
				"query_channel",b.getInterrogationChannel(),
				"reply_channel",b.getReplayChannel(),
				"transponder_delay",b.getTransponderDelay()
				);
		} catch (Exception e) {
			e.printStackTrace();
			m=null;
		}
		System.out.println(m.toString());
		return m;
	}

	@Override
	public void onBeaconListChange(ArrayList<Beacon> list) {
		setBeaconList(list);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		Log.i(TAG,"list item clicked!");
		BeaconDialog dialog = new BeaconDialog(context, (Beacon)parent.getAdapter().getItem(position));
		dialog.setOnDismissListener(this);
		dialog.show();
	}

	@Override
	public void onDismiss(DialogInterface arg0) 
	{
		((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
	}

    public void onClick(DialogInterface dialog, int item) {
    	targetSys = Accu.getInstance().getSystemList().findSysByName(items[item]);
    	sendBeaconConfig();
    }
}


