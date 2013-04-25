package com.snaplion.scorecard.entities;

import java.util.ArrayList;

import org.json.JSONObject;


public class Innings 
{
	Inning first;
	Inning superover;
	
	JSONObject json;
	public Innings(){}
	public Innings(JSONObject json)
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
			first=json.has("1")?new Inning(json.getJSONObject("1")):null;
			superover=json.has("superover")?new Inning(json.getJSONObject("superover")):null;
		}
		catch(Exception ex){ex.printStackTrace();}
	}
	public Inning getFirst() {
		return first;
	}
	public Inning getSuperover() {
		return superover;
	}
	public JSONObject getJson() {
		return json;
	}
	
	
}
