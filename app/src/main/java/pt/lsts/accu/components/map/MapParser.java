package pt.lsts.accu.components.map;

import java.util.ArrayList;

import pt.lsts.imc.IMCMessage;

public class MapParser 
{
	private ArrayList<MapFeature> featureList = new ArrayList<MapFeature>();
	
	public static final String TAG = "MapParser";
	public MapParser(IMCMessage msg)
	{
		IMCMessage features = msg.getMessage("features");
		for(IMCMessage feat = features; feat!=null; feat = feat.getMessage("next"))
		{
			featureList.add(new MapFeature(feat.getMessage("msg")));
		}
	}
	public ArrayList<MapFeature> getFeatureList()
	{
		return featureList;
	}
}
