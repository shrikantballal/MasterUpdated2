package com.snaplion.scorecard.entities;

import org.json.JSONObject;

public class Ball 
{
	private String calc__overs;
	private String calc__batsman__four;
	private String calc__bowler__extras;
	private String calc__batsman__out_at;
	private String over;
	private String calc__batsman__dotball;
	private String calc__bowler__runs;
	private String calc__team__balls;
	private String ball_type;
	private String calc__team__runs;
	private String calc__bowler__wicket;
	private String batsman_a;
	private String batsman_b;
	private String innings;
	private String calc__bowler__balls;
	private String wicket_type;
	private String comments;
	private String wicket;
	private String calc__bowler___key;
	private String match;
	private String boundry;
	private String ball;
	private String calc__batsman__six;
	private String calc__batsman__balls;
	private String bowler;
	private String key;
	private String fielder;
	private String calc__team__extras;
	private String calc__batsman__runs;
	private String runs;
	private String batting_team;
	private String extras;
	private String status;
	private String calc__bowler__b_repr;
	private String calc__team__wicket;
	private String calc__batsman___key;
	
	JSONObject json;
	public Ball(){}
	public Ball(JSONObject json)
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
			calc__overs=json.has("calc__overs")?json.getString("calc__overs"):null;
			calc__batsman__four=json.has("calc__batsman__four")?json.getString("calc__batsman__four"):null;
			calc__bowler__extras=json.has("calc__bowler__extras")?json.getString("calc__bowler__extras"):null;
			calc__batsman__out_at=json.has("calc__batsman__out_at")?json.getString("calc__batsman__out_at"):null;
			over=json.has("over")?json.getString("over"):null;
			calc__batsman__dotball=json.has("calc__batsman__dotball")?json.getString("calc__batsman__dotball"):null;
			calc__bowler__runs=json.has("calc__bowler__runs")?json.getString("calc__bowler__runs"):null;
			calc__team__balls=json.has("calc__team__balls")?json.getString("calc__team__balls"):null;
			ball_type=json.has("ball_type")?json.getString("ball_type"):null;
			calc__team__runs=json.has("calc__team__runs")?json.getString("calc__team__runs"):null;
			calc__bowler__wicket=json.has("calc__bowler__wicket")?json.getString("calc__bowler__wicket"):null;
			batsman_a=json.has("batsman_a")?json.getString("batsman_a"):null;
			batsman_b=json.has("batsman_b")?json.getString("batsman_b"):null;
			innings=json.has("innings")?json.getString("innings"):null;
			calc__bowler__balls=json.has("calc__bowler__balls")?json.getString("calc__bowler__balls"):null;
			wicket_type=json.has("wicket_type")?json.getString("wicket_type"):null;
			comments=json.has("comments")?json.getString("comments"):null;
			wicket=json.has("wicket")?json.getString("wicket"):null;
			calc__bowler___key=json.has("calc__bowler___key")?json.getString("calc__bowler___key"):null;
			match=json.has("match")?json.getString("match"):null;
			boundry=json.has("boundry")?json.getString("boundry"):null;
			ball=json.has("ball")?json.getString("ball"):null;
			calc__batsman__six=json.has("calc__batsman__six")?json.getString("calc__batsman__six"):null;
			calc__batsman__balls=json.has("calc__batsman__balls")?json.getString("calc__batsman__balls"):null;
			bowler=json.has("bowler")?json.getString("bowler"):null;
			key=json.has("key")?json.getString("key"):null;
			fielder=json.has("fielder")?json.getString("fielder"):null;
			calc__team__extras=json.has("calc__team__extras")?json.getString("calc__team__extras"):null;
			calc__batsman__runs=json.has("calc__batsman__runs")?json.getString("calc__batsman__runs"):null;
			runs=json.has("runs")?json.getString("runs"):null;
			batting_team=json.has("batting_team")?json.getString("batting_team"):null;
			extras=json.has("extras")?json.getString("extras"):null;
			status=json.has("status")?json.getString("status"):null;
			calc__bowler__b_repr=json.has("calc__bowler__b_repr")?json.getString("calc__bowler__b_repr"):null;
			calc__team__wicket=json.has("calc__team__wicket")?json.getString("calc__team__wicket"):null;
			calc__batsman___key=json.has("calc__batsman___key")?json.getString("calc__batsman___key"):null;
		}
		catch(Exception ex){ex.printStackTrace();}
	}
	public String getCalc__overs() {
		return calc__overs;
	}
	public String getCalc__batsman__four() {
		return calc__batsman__four;
	}
	public String getCalc__bowler__extras() {
		return calc__bowler__extras;
	}
	public String getCalc__batsman__out_at() {
		return calc__batsman__out_at;
	}
	public String getOver() {
		return over;
	}
	public String getCalc__batsman__dotball() {
		return calc__batsman__dotball;
	}
	public String getCalc__bowler__runs() {
		return calc__bowler__runs;
	}
	public String getCalc__team__balls() {
		return calc__team__balls;
	}
	public String getBall_type() {
		return ball_type;
	}
	public String getCalc__team__runs() {
		return calc__team__runs;
	}
	public String getCalc__bowler__wicket() {
		return calc__bowler__wicket;
	}
	public String getBatsman_a() {
		return batsman_a;
	}
	public String getBatsman_b() {
		return batsman_b;
	}
	public String getInnings() {
		return innings;
	}
	public String getCalc__bowler__balls() {
		return calc__bowler__balls;
	}
	public String getWicket_type() {
		return wicket_type;
	}
	public String getComments() {
		return comments;
	}
	public String getWicket() {
		return wicket;
	}
	public String getCalc__bowler___key() {
		return calc__bowler___key;
	}
	public String getMatch() {
		return match;
	}
	public String getBoundry() {
		return boundry;
	}
	public String getBall() {
		return ball;
	}
	public String getCalc__batsman__six() {
		return calc__batsman__six;
	}
	public String getCalc__batsman__balls() {
		return calc__batsman__balls;
	}
	public String getBowler() {
		return bowler;
	}
	public String getKey() {
		return key;
	}
	public String getFielder() {
		return fielder;
	}
	public String getCalc__team__extras() {
		return calc__team__extras;
	}
	public String getCalc__batsman__runs() {
		return calc__batsman__runs;
	}
	public String getRuns() {
		return runs;
	}
	public String getBatting_team() {
		return batting_team;
	}
	public String getExtras() {
		return extras;
	}
	public String getStatus() {
		return status;
	}
	public String getCalc__bowler__b_repr() {
		return calc__bowler__b_repr;
	}
	public String getCalc__team__wicket() {
		return calc__team__wicket;
	}
	public String getCalc__batsman___key() {
		return calc__batsman___key;
	}
	public JSONObject getJson() {
		return json;
	}
	
	
}
