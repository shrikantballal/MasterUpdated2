package com.snaplion.scorecard.entities;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import com.snaplion.scorecard.ScorecardManager;


public class Match implements Comparable<Match>
{
	Date matchDate;
	private MatchInfo info;
	private ArrayList<String> commands;
	private double ts;
	private String bat_team_name;
	private Team team_a;
	private Team team_b;
	private String bowl_team_name;
	//private String bat_team_name;
	//private String bowl_team_name;
	//private ArrayList<String> man_of_match_data;
	JSONObject json;
	public Match(){}
	public Match(JSONObject json)
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
			info=json.has("info")?new MatchInfo(json.getJSONObject("info")):null;
			matchDate=info.getStart_date()!=null?ScorecardManager.getInstance().getDateObject(info.getStart_date()):null;
			commands=json.has("commands")?ScorecardManager.getInstance().getList(json.getJSONArray("commands")):null;
			ts=json.has("ts")?json.getDouble("ts"):null;
			bat_team_name=json.has("bat_team_name")?json.getString("bat_team_name"):null;
			team_a=json.has("team_a")?new Team(json.getJSONObject("team_a")):null;
			team_b=json.has("team_b")?new Team(json.getJSONObject("team_b")):null;
			bowl_team_name=json.has("bat_team_name")?json.getString("bowl_team_name"):null;
		}
		catch(Exception ex){ex.printStackTrace();}
	}
	public MatchInfo getInfo() {
		return info;
	}
	public ArrayList<String> getCommands() {
		return commands;
	}
	public double getTs() {
		return ts;
	}
	public Team getTeam_a() {
		return team_a;
	}
	public Team getTeam_b() {
		return team_b;
	}
	public JSONObject getJson() {
		return json;
	}
	public String getBat_team_name() {
		return bat_team_name;
	}
	public String getBowl_team_name() {
		return bowl_team_name;
	}
	@Override
	public int compareTo(Match another) 
	{
		if(this.matchDate.after(another.matchDate))
		{
			return 1;
		}
		else if(this.matchDate.before(another.matchDate))
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
	
	
}
