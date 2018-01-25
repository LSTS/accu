package pt.lsts.accu.util;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LayoutUtil {

	public static ArrayList<View> flattenLayout(View view)
	{
		ArrayList<View> viewList = new ArrayList<View>();
		
		if(view instanceof ViewGroup)
		{
			if(((ViewGroup)view).getChildCount()==0)
				viewList.add(view);
			else
			{
				viewList.add(view);
				ViewGroup viewgroup = (ViewGroup) view;
				for(int i = 0; i < viewgroup.getChildCount();i++)
				{
					viewList.addAll(flattenLayout(viewgroup.getChildAt(i)));
				}
			}	
		}
		else if(view instanceof View)
		{
			viewList.add(view);
		}
		return viewList;
	}
	public static ArrayList<View> flattenLayout(View view, boolean addViewGroups)
	{
		ArrayList<View> viewList = new ArrayList<View>();
		if(view instanceof ViewGroup)
		{
			if(((ViewGroup)view).getChildCount()==0)
				viewList.add(view);
			else
			{
				if(addViewGroups)
				{
					viewList.add(view);
				}
				ViewGroup viewgroup = (ViewGroup) view;
				for(int i = 0; i < viewgroup.getChildCount();i++)
				{
					viewList.addAll(flattenLayout(viewgroup.getChildAt(i),false));
				}
			}	
		}
		else if(view instanceof View)
		{
			viewList.add(view);
		}
		return viewList;
	}
	public static void fillLayoutField(ViewGroup viewgroup, String tag, Object value)
	{
		View view = viewgroup.findViewWithTag(tag);
		if(view instanceof TextView)
		{
			((TextView)view).setText((String)value);
		}
	}
}
