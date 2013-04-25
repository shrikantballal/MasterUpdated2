package com.snaplion.scorecard.entities;

import java.util.ArrayList;

import org.json.JSONObject;

import com.snaplion.scorecard.ScorecardManager;

public class AllMatches 
{
	private String card_name;
	private Matches matches;
	private String series;
	private String venue;
	private ArrayList<String> teams;
	private String uts;
	private String key;
	private ArrayList<String> rounds;
	private String name;
	
	JSONObject json;
	public AllMatches(){}
	public AllMatches(JSONObject json)
	{init(json);}
	public void init(JSONObject json)
	{
		this.json=json;
		if(json==null)
			return;
		parse();
	}
	private void parse() 
	{
		try
		{
			card_name=json.has("card_name")?json.getString("card_name"):null;
			matches=json.has("matches")?new Matches(json.getJSONObject("matches")):null;
			series=json.has("series")?json.getString("series"):null;
			venue=json.has("venue")?json.getString("venue"):null;
			teams=json.has("teams")?ScorecardManager.getInstance().getList(json.getJSONArray("teams")):null;
			uts=json.has("uts")?json.getString("uts"):null;
			key=json.has("key")?json.getString("key"):null;
			rounds=json.has("rounds")?ScorecardManager.getInstance().getList(json.getJSONArray("rounds")):null;
			name=json.has("name")?json.getString("name"):null;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public String getCard_name() {
		return card_name;
	}
	public Matches getMatches() {
		return matches;
	}
	public String getSeries() {
		return series;
	}
	public String getVenue() {
		return venue;
	}
	public ArrayList<String> getTeams() {
		return teams;
	}
	public String getUts() {
		return uts;
	}
	public String getKey() {
		return key;
	}
	public ArrayList<String> getRounds() {
		return rounds;
	}
	public String getName() {
		return name;
	}
	public JSONObject getJson() {
		return json;
	}
	
	
}
