package pt.lsts.accu.console;

import java.util.ArrayList;

import pt.lsts.accu.Main;
import pt.lsts.accu.panel.AccuBasePanel;
import pt.up.fe.dceg.accu.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ericharlow.DragNDrop.DragNDropAdapter;
import com.ericharlow.DragNDrop.DragNDropListView;
import com.ericharlow.DragNDrop.DropListener;

public class ConsoleConfigurator extends AccuBasePanel implements DropListener, OnItemClickListener
{
	DragNDropListView dragList;
	ArrayList<PanelConfigItem> panelList;
	
	public ConsoleConfigurator(Context context) {
		super(context);
		dragList = (DragNDropListView) getLayout().findViewWithTag("draglist");
		dragList.setDropListener(this);
		dragList.setOnItemClickListener(this);
//		dragList.setClickable(true);
//		dragList.setFocusable(false);
		dragList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
	}
	
	@Override
	public void onStart() 
	{
		panelList = ((Main)getContext()).getConsoleConfig().panelList;
		dragList.setAdapter(new DragNDropAdapter() {	
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final PanelConfigItem pci = getItem(position);
				View v = convertView;
				if(v == null)
					v = LayoutInflater.from(getContext()).inflate(R.layout.dragitem, null);
				((TextView)v.findViewWithTag("name")).setText(pci.getName());
				((ImageView)v.findViewWithTag("icon")).setImageResource(pci.getIcon());
				CheckBox cb = ((CheckBox)v.findViewWithTag("check"));
				cb.setChecked(pci.active);
				cb.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) 
					{
						pci.active = !pci.active;
						((BaseAdapter)dragList.getAdapter()).notifyDataSetChanged();
					}
				});
				
				v.setFocusable(false);
				return v;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public PanelConfigItem getItem(int position) {
				return panelList.get(position);
			}
			
			@Override
			public int getCount() {
				return panelList.size();
			}
			
			@Override
			public void onRemove(int which) {
				panelList.remove(which);
			}
			
			public void onDrop(int from, int to) {
				System.out.println("called");
				PanelConfigItem temp = panelList.get(from);
				panelList.remove(from);
				panelList.add(to,temp);
			}
		});
		((BaseAdapter)dragList.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onStop() 
	{

	}

	@Override
	public View buildLayout() {
		return inflateFromResource(R.layout.console_configurator_layout);
	}

	@Override
	public int getIcon() {
		return R.drawable.preferences_icon_1;
	}

	@Override
	public void onDrop(int from, int to) 
	{
		System.out.println(from + " " + to);
		((DragNDropAdapter)dragList.getAdapter()).onDrop(from, to);
//		for(PanelConfigItem pci : panelList)
//			System.out.println(pci.getName());
		dragList.invalidateViews();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		System.out.println(arg1);	
	}
	@Override
	public void prepareMenu(Menu menu)
	{
		menu.clear();
		menu.add("Save Config");
	}
	
	@Override 
	public void menuHandler(MenuItem item)
	{
		if(item.getTitle().equals("Save Config"))
		{
			((Main)getContext()).getConsoleConfig().updateConfig();
		}
	}
}
