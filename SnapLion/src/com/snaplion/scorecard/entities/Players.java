package com.snaplion.scorecard.entities;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

public class Players 
{
	ArrayList<Player> players;
	ArrayList<String> batting_order;
	ArrayList<String> bowling_order;
	JSONObject json;
	public Players(){}
	public Players(JSONObject json, ArrayList<String> batting_order, ArrayList<String> bowling_order)
	{
		init(json,batting_order,bowling_order);
	}
	public void init(JSONObject json,ArrayList<String> batting_order, ArrayList<String> bowling_order)
	{
		this.json=json;
		this.batting_order=batting_order;
		this.bowling_order=bowling_order;
		if(json==null)return;
		
		parse();
	}
	private void parse() 
	{
		try
		{
			players=new ArrayList<Player>();
			Iterator<String> itr = json.keys();
			while(itr.hasNext())
			{
				String key = itr.next();
				players.add(new Player(json.getJSONObject(key)));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public ArrayList<Player> getPlayerByBowlingOrder()
	{
		try
		{
			ArrayList<Player> players2=new ArrayList<Player>();
			for(String player_name:bowling_order)
			{
				players2.add(new Player(json.getJSONObject(player_name)));
			}
			return players2;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	public ArrayList<Player> getPlayerByBattingOrder()
	{
		try
		{
			ArrayList<Player> players1=new ArrayList<Player>();
			for(String player_name:batting_order)
			{
				players1.add(new Player(json.getJSONObject(player_name)));
			}
			return players1;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	public ArrayList<Player> getPlayers() {
		return players;
	}
	public JSONObject getJson() {
		return json;
	}
}
