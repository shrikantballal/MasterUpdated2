package com.snaplion.scorecard.entities;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

public class Matches 
{
	ArrayList<Match> matches;
	JSONObject json;
	public Matches(){}
	public Matches(JSONObject json)
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
			matches=new ArrayList<Match>();
			Iterator<String> itr = json.keys();
			while(itr.hasNext())
			{
				String key = itr.next();
				matches.add(new Match(json.getJSONObject(key)));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public ArrayList<Match> getMatches() {
		return matches;
	}
	public JSONObject getJson() {
		return json;
	}
	
	
}
