package com.snaplion.scorecard.entities;

import java.util.ArrayList;

import org.json.JSONObject;

public class Team 
{
	private Innings innings;//#TODO check;
	private String runs;
	private String balls;
	private String name;
	private String card_name;
	private String team;
	private String req_runs_balls;
	private String wicket;
	private String req_runs;
	private String extras;
	private String req_runs_str;
	private String req_runs_rare;
	private String run_string;
	
	JSONObject json;
	public Team(){}
	public Team(JSONObject json)
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
			innings=json.has("innings")?new Innings(json.getJSONObject("innings")):null;
			runs=json.has("runs")?json.getString("runs"):"0";
			balls=json.has("balls")?json.getString("balls"):null;
			name=json.has("name")?json.getString("name"):null;
			card_name=json.has("card_name")?json.getString("card_name"):null;
			team=json.has("team")?json.getString("team"):null;
			req_runs_balls=json.has("req_runs_balls")?json.getString("req_runs_balls"):null;
			wicket=json.has("wicket")?json.getString("wicket"):"0";
			req_runs=json.has("req_runs")?json.getString("req_runs"):null;
			extras=json.has("extras")?json.getString("extras"):"0";
			req_runs_str=json.has("req_runs_str")?json.getString("req_runs_str"):null;
			req_runs_rare=json.has("req_runs_rare")?json.getString("req_runs_rare"):null;
			run_string=json.has("run_string")?json.getString("run_string"):null;
		}
		catch(Exception ex){ex.printStackTrace();}
	}
	public Innings getInnings() {
		return innings;
	}
	public String getRuns() {
		return runs;
	}
	public String getBalls() {
		return balls;
	}
	public String getName() {
		return name;
	}
	public String getCard_name() {
		return card_name;
	}
	public String getTeam() {
		return team;
	}
	public String getReq_runs_balls() {
		return req_runs_balls;
	}
	public String getWickets() {
		return wicket;
	}
	public String getReq_runs() {
		return req_runs;
	}
	public String getExtras() {
		return extras;
	}
	public String getReq_runs_str() {
		return req_runs_str;
	}
	public String getReq_runs_rare() {
		return req_runs_rare;
	}
	public String getRun_string() {
		return run_string;
	}
	public JSONObject getJson() {
		return json;
	}
}
