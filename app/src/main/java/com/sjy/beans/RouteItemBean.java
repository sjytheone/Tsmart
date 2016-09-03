package com.sjy.beans;

import java.io.Serializable;

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

	public String getStrStationID(){return strStationID;}
	public void setStrStationID(String strID){this.strStationID = strID;}

	private String strStationID;
	private String strStationName;
	private String strStationDesc;

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


	public String getStationFragmentDes() {
		return StationFragmentDes;
	}

	public void setStationFragmentDes(String stationFragmentDes) {
		StationFragmentDes = stationFragmentDes;
	}

	private String StationFragmentDes;

}
