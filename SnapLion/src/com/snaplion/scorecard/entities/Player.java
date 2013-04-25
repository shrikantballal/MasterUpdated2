package com.snaplion.scorecard.entities;

import org.json.JSONObject;


public class Player 
{
	private String card_name;
	private String name;
	private String batting_style;
	private String bowling_style;
	private String out_str;
	private String role;
	private String key;
	private Bowling bowling;
	private Batting batting;
	
	JSONObject json;
	public Player(){}
	public Player(JSONObject json)
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
			card_name=json.has("card_name")?json.getString("card_name"):null;
			name=json.has("name")?json.getString("name"):null;
			batting_style=json.has("batting_style")?json.getString("batting_style"):null;
			bowling_style=json.has("bowling_style")?json.getString("bowling_style"):null;
			out_str=json.has("out_str")?json.getString("out_str"):null;
			role=json.has("role")?json.getString("role"):null;
			key=json.has("key")?json.getString("key"):null;
			bowling=json.has("bowling")?new Bowling(json.getJSONObject("bowling")):null;
			batting=json.has("batting")?new Batting(json.getJSONObject("batting")):null;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public String getOut_str() {
		return out_str;
	}
	public String getCard_name() {
		return card_name;
	}
	public String getName() {
		return name;
	}
	public String getBatting_style() {
		return batting_style;
	}
	public String getBowling_style() {
		return bowling_style;
	}
	public String getRole() {
		return role;
	}
	public String getKey() {
		return key;
	}
	public Bowling getBowling() {
		return bowling;
	}
	public Batting getBatting() {
		return batting;
	}
	public JSONObject getJson() {
		return json;
	}
	
	
}
