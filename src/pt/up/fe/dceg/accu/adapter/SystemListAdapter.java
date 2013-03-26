package pt.up.fe.dceg.accu.adapter;

import java.util.ArrayList;

import pt.up.fe.dceg.accu.R;
import pt.up.fe.dceg.accu.state.Accu;
import pt.up.fe.dceg.accu.types.Sys;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Adapter class derived from baseAdapter that uses a system list to generate the views
 * Usable with every AdapterView
 * @author jqcorreia
 *
 */
public class SystemListAdapter extends BaseAdapter {

	static final boolean DEBUG = false; 
	
	Context mContext;
	ArrayList<Sys> mList;
	ColorMatrix cm = new ColorMatrix();
	ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(cm);
	
	public SystemListAdapter(Context context, ArrayList<Sys> list)
	{
		mContext = context;
		setList(list);
	}
	
	public void setList(ArrayList<Sys> list)
	{
		mList = list;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Sys getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if(DEBUG) System.out.println("getview()");
		
		Sys v = getItem(position);
		
		View view = convertView;
		if(view==null)
			view = LayoutInflater.from(mContext).inflate(R.layout.syslist_item_layout, parent,false);
		
		LinearLayout container = (LinearLayout) view.findViewById(R.id.syslist_ll);
		TextView tvName = (TextView) view.findViewById(R.id.syslist_name);
		TextView tvInfo = (TextView) view.findViewById(R.id.syslist_info);
		
		tvName.setTextSize(20);
		// Set color to white for disconnected not erroneous not CCU systems (visibility)
		tvName.setTextColor((!v.isConnected()&&!v.isError()&&!v.getType().equals("CCU"))? Color.WHITE : Color.BLACK); 
		tvName.setText(v.getName() + (v==Accu.getInstance().getActiveSys()?"(M)":""));
		tvInfo.setText("Connected: " + v.isConnected() + " Error: " + v.isError()+ " Address: " + v.getAddress()+":"+v.getPort());
		
		GradientDrawable shape = (GradientDrawable) container.getBackground();
		
		if(v.getType().equalsIgnoreCase("CCU")) //FIXME for now just distinguishes CCU&Vehicles
		{
			cm.set(createMatrix("77F171"));
		}
		else
		{
			if(v.isConnected())
			{
				if(v.isError())
				{
					//CONNECTED - ERROR
					cm.set(createMatrix("FE8E0A"));
				}
				else
				{
					//CONNECTED - NO ERROR
					cm.set(createMatrix("0FA4FF"));
				}
			}
			else
			{
				if(v.isError())
				{
					// NOT CONNECTED - ERROR
					cm.set(createMatrix("FF0000"));

				}
				else
				{
					// NOT CONNECTED - NO ERROR
					cm.set(createMatrix("002841"));
				}
			}
		}
		
		shape.setColorFilter(new ColorMatrixColorFilter(cm));
		container.setBackgroundDrawable(shape);
		return container;
	}
	float[] createMatrix(int r, int g, int b)
	{
		return new float[] {0,0,0,0,r,0,0,0,0,g,0,0,0,0,b,1,1,1,1,255};
	}
	float[] createMatrix(String hex)
	{
		int r = Integer.parseInt(hex.substring(0, 2),16);
		int g = Integer.parseInt(hex.substring(2, 4),16);
		int b = Integer.parseInt(hex.substring(4, 6),16);
//		System.out.println(r + " " + g + " " + b);
		return createMatrix(r,g,b);
	}
}
