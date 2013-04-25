package com.snaplion.scorecard.entities;

import org.json.JSONObject;

public class Batting 
{
	private String runs;
	private String balls;
	private String dotball;
	private String six;
	private String four;
	
	JSONObject json;
	public Batting(){}
	public Batting(JSONObject json)
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
			dotball=json.has("dotball")?json.getString("dotball"):"0";
			six=json.has("six")?json.getString("six"):"0";
			four=json.has("four")?json.getString("four"):"0";
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
	public String getDotball() {
		return dotball;
	}
	public String getSix() {
		return six;
	}
	public String getFour() {
		return four;
	}
	public JSONObject getJson() {
		return json;
	}
	
	
}
