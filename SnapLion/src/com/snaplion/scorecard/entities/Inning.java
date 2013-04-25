package com.snaplion.scorecard.entities;

import java.util.ArrayList;

import org.json.JSONObject;

import com.snaplion.scorecard.ScorecardManager;

public class Inning 
{
	private ArrayList<String> batting_order;
	private String runs;
	private ArrayList<String> fall_of_wicket;
	private String dotball;
	private String six;
	private String wickets;
	private String four;
	private Players players;
	private String balls;
	private ArrayList<String> bowling_order;
	private String run_string;
	private String overs;
	private String run_rate;
	private String extras;
	
	JSONObject json;
	public Inning(){}
	public Inning(JSONObject json)
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
			batting_order=json.has("batting_order")?ScorecardManager.getInstance().getList(json.getJSONArray("batting_order")):null;
			runs=json.has("runs")?json.getString("runs"):null;
			fall_of_wicket=json.has("fall_of_wicket")?ScorecardManager.getInstance().getList(json.getJSONArray("fall_of_wicket")):null;
			dotball=json.has("dotball")?json.getString("dotball"):null;
			six=json.has("six")?json.getString("six"):null;
			wickets=json.has("wickets")?json.getString("wickets"):null;
			four=json.has("four")?json.getString("four"):null;
			balls=json.has("balls")?json.getString("balls"):null;
			bowling_order=json.has("bowling_order")?ScorecardManager.getInstance().getList(json.getJSONArray("bowling_order")):null;
			run_string=json.has("run_string")?json.getString("run_string"):null;
			overs=json.has("overs")?json.getString("overs"):"0";
			run_rate=json.has("run_rate")?json.getString("run_rate"):null;
			extras=json.has("extras")?json.getString("extras"):null;
			players=json.has("players")?new Players(json.getJSONObject("players"), batting_order,bowling_order):null;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public ArrayList<String> getBatting_order() {
		return batting_order;
	}
	public String getRuns() {
		return runs;
	}
	public ArrayList<String> getFall_of_wicket() {
		return fall_of_wicket;
	}
	public String getDotball() {
		return dotball;
	}
	public String getSix() {
		return six;
	}
	public String getWickets() {
		return wickets;
	}
	public String getFour() {
		return four;
	}
	public Players getPlayers() {
		return players;
	}
	public String getBalls() {
		return balls;
	}
	public ArrayList<String> getBowling_order() {
		return bowling_order;
	}
	public String getRun_string() {
		return run_string;
	}
	public String getOvers() {
		return overs;
	}
	public String getRun_rate() {
		return run_rate;
	}
	public String getExtras() {
		return extras;
	}
	public JSONObject getJson() {
		return json;
	}
	
	
}
