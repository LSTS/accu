package pt.lsts.accu.console;

import pt.lsts.accu.panel.AccuAction;
import pt.up.fe.dceg.accu.R;


public class PanelConfigItem implements Comparable<PanelConfigItem>
{
	public Class<?> panelClass;
	public boolean active;
	public int order;
	
	public PanelConfigItem(Class<?> pClass, boolean active, int order)
	{
		panelClass = pClass;
		this.active = active;
		this.order = order;
	}

	public String getName()
	{
		AccuAction a = panelClass.getAnnotation(AccuAction.class);
		return (a!=null ? a.name() : "default action name");
	}
	public int getIcon()
	{
		AccuAction a = panelClass.getAnnotation(AccuAction.class);
		return (a!=null ? a.icon() : R.drawable.icon);
	}
	public boolean isActive()
	{
		return active;
	}
	public int getOrder()
	{
		return order;
	}
	
	@Override
	public int compareTo(PanelConfigItem another) {
		return this.order - another.order;
	}
	
	@Override
	public String toString()
	{
		return "Name : " + getName() + " active :" + isActive() + " order: " + order;
	}
}