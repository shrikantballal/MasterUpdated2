package com.snaplion.scorecard.entities;

import org.json.JSONObject;

public class Bowling 
{
	private String runs;
	private String balls;
	private String wicket;
	private String extras;
	private String overs;
	
	JSONObject json;
	public Bowling(){}
	public Bowling(JSONObject json)
	{init(json);}
	public void init(JSONObject json)
	{
		this.json=json;
		if(json==null)return;
		parse();
	}
	private void parse() 
	{
		try
		{
			runs=json.has("runs")?json.getString("runs"):"0";
			balls=json.has("balls")?json.getString("balls"):"0";
			wicket=json.has("wicket")?json.getString("wicket"):"0";
			extras=json.has("extras")?json.getString("extras"):"0";
			overs=json.has("overs")?json.getString("overs"):null;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public String getRuns() {
		return runs;
	}
	public String getBalls() {
		return balls;
	}
	public String getWicket() {
		return wicket;
	}
	public String getExtras() {
		return extras;
	}
	public String getOvers() {
		return overs;
	}
	public JSONObject getJson() {
		return json;
	}
	
	
}
