package pt.lsts.accu.adapter;

import java.util.ArrayList;

import pt.lsts.accu.state.Accu;
import pt.lsts.accu.types.Sys;
import pt.lsts.accu.R;
import pt.lsts.accu.util.AccuTimer;

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
    private AccuTimer accuTimer;
	
	Context mContext;
	ArrayList<Sys> mList;
	ColorMatrix cm = new ColorMatrix();
	ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(cm);
    private int itemPressed = -1;

    public int getItemPressed() {
        return itemPressed;
    }

    public void setItemPressed(int itemPressed) {
        this.itemPressed = itemPressed;
    }

    public SystemListAdapter(Context context, ArrayList<Sys> list)
	{
		mContext = context;
		setList(list);
        initTimer();
	}

    public void initTimer(){
        accuTimer = new AccuTimer(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 100);
        accuTimer.start();
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
		//tvName.setTextColor((!v.isConnected()&&!v.isError()&&!v.getType().equals("CCU"))? Color.WHITE : Color.BLACK);
		tvName.setText(v.getName() + " - " + v.getType() + (v==Accu.getInstance().getActiveSys()?" (M)":""));
        if (getItemPressed()==position){
            String text = "Address: " + v.getAddress()+":"+v.getPort();
            text += "\n";
            text += timeSinceLastMessage(System.currentTimeMillis(), v.lastMessageReceived);
            if (v.isConnected()==false)
                text += "\nConnected: " + v.isConnected();
            if (v.isError()==true)
                text += "\nError: " + v.isError();

            tvInfo.setText(text);
        }else{
            tvInfo.setText("");
        }
		
		GradientDrawable shape = (GradientDrawable) container.getBackground();

        //color codes
        final String ORANGE = "FF8000";//orange
        final String RED = "FF1428";//red
        //final String GREEN = "006400";//green
        final String BLUE = "2BB6E3";//cyan
        //final String BLUE_LIGHTER = "B5C6D8";//very light blue
        final String BLUE_DARKER = "15596F";//dark blue
        //final String OLD = "253F3F";//gray
        final String IDLE = "6E6E6E";//brighter gray
        final String BLACK = "#000000";//black
        final String WHITE = "#FFFFFF";//white

        tvName.setTextColor(Color.parseColor(BLACK));
        tvInfo.setTextColor(Color.parseColor(BLACK));
		if(v.getType().equalsIgnoreCase("CCU")) //FIXME for now just distinguishes CCU&Vehicles
		{
			cm.set(createMatrix(IDLE));
            tvName.setTextColor(Color.parseColor(WHITE));
            tvInfo.setTextColor(Color.parseColor(WHITE));
		}
		else
		{
			if(v.isConnected())
			{
				if(v.isError())
				{
					//CONNECTED - ERROR
					cm.set(createMatrix(ORANGE));
				}
				else
				{
					//CONNECTED - NO ERROR
					cm.set(createMatrix(BLUE));
				}
			}
			else
			{
				if(v.isError())
				{
					// NOT CONNECTED - ERROR
					cm.set(createMatrix(RED));

				}
				else
				{
					// NOT CONNECTED - NO ERROR
					cm.set(createMatrix(BLUE_DARKER));
                    tvName.setTextColor(Color.parseColor(WHITE));
                    tvInfo.setTextColor(Color.parseColor(WHITE));
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

    public static String timeSinceLastMessage(long t1, long t2){
        String string="Time since last message: ";
        long hours=0;
        long minutes=0;
        long seconds = 0;
        long millisec = t1-t2;
        if (millisec>1000){
            seconds = millisec/1000;
            if (seconds>60){
                minutes = seconds/60;
                if (minutes>60){
                    hours = minutes/60;
                    minutes = minutes%60;
                }
                seconds = seconds%60;
            }
            millisec = millisec%1000;
        }
        if (hours>0){
            string += hours+"h ";
        }
        if (minutes>0){
            string += minutes+"m ";
        }
        if (seconds>0){
            string += seconds+"s ";
        }
        if (millisec>0){
            string += millisec+"ms";
        }

        return string;
    }
}

