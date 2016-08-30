package com.sjy.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RouteItemBean implements Serializable{

	public String getStrStationName() {
		return strStationName;
	}
	public void setStrStationName(String strStationName) {
		this.strStationName = strStationName;
	}
	public String getStrStationDesc() {
		return strStationDesc;
	}
	public void setStrStationDesc(String strStationDesc) {
		this.strStationDesc = strStationDesc;
	}
	public int getnBusCount() {
		return nBusCount;
	}
	public void setnBusCount(int nBusCount) {
		this.nBusCount = nBusCount;
	}
	public Integer getnStationOlder() {
		return nStationOlder;
	}

	public void setnStationOlder(int nStationOlder) {
		this.nStationOlder = nStationOlder;
	}
	public String getStrStationID(){return strStationID;}
	public void setStrStationID(String strID){this.strStationID = strID;}

	private String strStationID;
	private String strStationName;
	private String strStationDesc;
	private int    nBusCount;
	private Integer    nStationOlder = -1;

	private Map<String,Integer> mRouteOder = new HashMap<>();

	public String getStrArrayTime() {
		return strArrayTime;
	}

	public void setStrArrayTime(String strArrayTime) {
		this.strArrayTime = strArrayTime;
	}

	private String strArrayTime;

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	private double lat = 0;

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	private double lon = 0;

	public void AddRouteOder(String route, int oder){
		mRouteOder.put(route,oder);
	}

	public int getRouteOder(String strRouteID){
		int oder = -1;
		if (mRouteOder.containsKey(strRouteID)){
			oder = mRouteOder.get(strRouteID);
		}
		return  oder;
	}

	public boolean isTailArrived() {
		return bTailArrived;
	}

	public void setTailArrived(boolean bTailArrived) {
		this.bTailArrived = bTailArrived;
	}

	private boolean bTailArrived = false;

	public String getStationFragmentDes() {
		return StationFragmentDes;
	}

	public void setStationFragmentDes(String stationFragmentDes) {
		StationFragmentDes = stationFragmentDes;
	}

	private String StationFragmentDes;

	public String getRouteLineIDs(){
		String ids = "";
		Set<String> keySet = mRouteOder.keySet();
		for (String s : keySet){
			ids += s + ";";
		}
		return ids;
	}
}
