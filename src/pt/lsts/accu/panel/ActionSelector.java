package pt.lsts.accu.panel;

import java.util.ArrayList;

import pt.lsts.accu.state.Accu;
import pt.lsts.accu.state.MainSysChangeListener;
import pt.lsts.accu.types.Sys;
import pt.lsts.accu.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ActionSelector extends LinearLayout 
implements OnItemClickListener, OnClickListener, MainSysChangeListener{

	ListView listView;
	ArrayList<AccuBaseAction> list;
	private Context context;	
	PanelItemClickListener listener;
	boolean isOpen = false;
	boolean isOpenerVisible = true;
	Button opener;
	ColorMatrixColorFilter defaultFilter; // For 'disabling' buttons
	ColorMatrixColorFilter disabledFilter; // For 'disabling' buttons
	private int selectedPanelColor = Color.parseColor("#ff9900"); // Color for selected panel icon background
//	private int defaultPanelColor = Color.parseColor("#DD000000");
	Toast toast;
	public BaseAdapter adapter = new BaseAdapter(){
		

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			
			if(view == null)
			{
				view = LayoutInflater.from(context).inflate(R.layout.panel_selector_item, null, false);
			}
			
			view.setBackgroundColor(Color.WHITE); // Default color
			AccuBaseAction item = (AccuBaseAction) getItem(position);
			int icon = item.getIcon();
			Drawable draw = context.getResources().getDrawable(icon);
			draw.setColorFilter(defaultFilter);
			
			if(currentSys == null && item.requiresActiveSys())
			{
				draw.setColorFilter(disabledFilter);
			}
			if(item instanceof AccuBasePanel)
			{
				if(((AccuBasePanel)item).isRunning())
				{
					view.setBackgroundColor(selectedPanelColor);
				}
			}
			ImageView img = (ImageView)view.findViewWithTag("icon");			
			img.setImageDrawable(draw);
			return view;
		}
	};
	
	private Sys currentSys;

	public ActionSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.panel_selector_layout,this,true);
		
		listView = (ListView)findViewWithTag("list");
		opener = (Button)findViewWithTag("button");
		opener.setOnClickListener(this);
		listView.setOnItemClickListener(this);
		Accu.getInstance().addMainSysChangeListener(this);
		
		// Initializations required for 'disabling' effect
		ColorMatrix cmDisabled = new ColorMatrix();
		cmDisabled.setSaturation(0);
		
		disabledFilter = new ColorMatrixColorFilter(cmDisabled);
		defaultFilter = new ColorMatrixColorFilter(new ColorMatrix());
		
		toast = Toast.makeText(context, "Please select a main vehicle.", 2000);
	}
	
	public void setPanelChangeListener(PanelItemClickListener l)
	{
		listener = l;
		
	}
	public void setList(ArrayList<AccuBaseAction> list)
	{
		this.list = list;
		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);
	}
	public void toggle()
	{
		if(isOpen)
			closeSelector();
		else
			openSelector();
	}
	public void openSelector()
	{
		listView.setVisibility(View.VISIBLE);
		opener.setGravity(0x30);
		isOpen=true;
	}
	public void closeSelector()
	{
		listView.setVisibility(View.GONE);
		opener.setGravity(0x50);
		isOpen=false;
	}
	
	public void setOpenerVisible(boolean visible)
	{
		if(visible)
			opener.setVisibility(View.VISIBLE);
		else
			opener.setVisibility(View.GONE);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AccuBaseAction item = (AccuBaseAction) parent.getAdapter().getItem(position);
		
		if(currentSys == null && item.requiresActiveSys())
		{
			toast.cancel();
			toast.show();
			return; // Do nothing
		}
		listener.onPanelItemClick(position);
		adapter.notifyDataSetChanged();
	}
	@Override
	public void onClick(View v) {
		toggle();
	}
	@Override
	public void onMainSysChange(Sys newMainSys) 
	{
		currentSys = newMainSys;
		adapter.notifyDataSetChanged();
	}	
}
