package com.snaplion.scorecard.entities;

import java.util.ArrayList;

import org.json.JSONObject;

import com.snaplion.scorecard.ScorecardManager;


public class MatchInfo 
{
	private ArrayList<ArrayList<String>> recent_overs;
	private String match_type;
	private String toss_asked_for;
	//private ArrayList<ArrayList<String>> batting_order;
	private String break_for;
	private String winner_team;
	private String start_date_small_str;
	private String team_a_captainkeeper;
	private Innings played_innings;
	private String toss_msg;
	private String _last_innings;
	private String match_type_name;
	private String skey;
	private String toss_won_by;
	private ArrayList<String> team_a_playing;
	private String thankyou_msg;
	private String current_innings;
	private String match_day;
	private String team_b_captainkeeper;
	private String team_a;
	private String team_b;
	private String start_date;
	private String status;
	private ArrayList<String> team_b_playing;
	private String man_of_match;
	private String required_runs_overs;
	private String season;
	private String delayed;
	private Ball next_ball;
	private String start_date_iso;
	private String datastatus;
	private String start_date_str;
	private String key;
	private String delta;
	private String date;
	private String welcome_msg;
	private String match_comments;
	private String pitch_report;
	private Ball _prev_ball;
	private String required_runs;
	private String sub_card_name;
	private String batting_team;
	private String name;
	private String title;
	private String venue;
	private Ball last_ball;
	private String season_name;
	private String first_batting;
	private String card_name;
	private String delta_str;
	private String break_duration;
	private String completed_by;
	
	JSONObject json;
	public MatchInfo(){}
	public MatchInfo(JSONObject json)
	{init(json);}
	public void init(JSONObject json)
	{
		this.json=json;
		parse();
	}
	private void parse() 
	{
		try
		{
			recent_overs=json.has("recent_overs")?ScorecardManager.getInstance().getList2(json.getJSONArray("recent_overs")):null;
			match_type=json.has("match_type")?json.getString("match_type"):null;
			toss_asked_for=json.has("toss_asked_for")?json.getString("toss_asked_for"):null;
			//batting_order=json.has("batting_order")?ScorecardManager.getInstance().getList2(json.getJSONArray("batting_order")):null;
			break_for=json.has("break_for")?json.getString("break_for"):null;
			winner_team=json.has("winner_team")?json.getString("winner_team"):null;
			start_date_small_str=json.has("start_date_small_str")?json.getString("start_date_small_str"):null;
			team_a_captainkeeper=json.has("team_a_captainkeeper")?json.getString("team_a_captainkeeper"):null;
			played_innings=json.has("played_innings")?new Innings(json.getJSONObject("played_innings")):null;
			toss_msg=json.has("toss_msg")?json.getString("toss_msg"):null;
			_last_innings=json.has("_last_innings")?json.getString("_last_innings"):null;
			match_type_name=json.has("match_type_name")?json.getString("match_type_name"):null;
			skey=json.has("skey")?json.getString("skey"):null;
			toss_won_by=json.has("toss_won_by")?json.getString("toss_won_by"):null;
			team_a_playing=json.has("team_a_playing")?ScorecardManager.getInstance().getList(json.getJSONArray("team_a_playing")):null;
			thankyou_msg=json.has("thankyou_msg")?json.getString("thankyou_msg"):null;
			current_innings=json.has("current_innings")?json.getString("current_innings"):null;
			match_day=json.has("match_day")?json.getString("match_day"):null;
			team_b_captainkeeper=json.has("team_b_captainkeeper")?json.getString("team_b_captainkeeper"):null;
			team_a=json.has("team_a")?json.getString("team_a"):null;
			team_b=json.has("team_b")?json.getString("team_b"):null;
			start_date=json.has("start_date")?json.getString("start_date"):null;
			status=json.has("status")?json.getString("status"):null;
			team_b_playing=json.has("team_b_playing")?ScorecardManager.getInstance().getList(json.getJSONArray("team_b_playing")):null;
			man_of_match=json.has("man_of_match")?json.getString("man_of_match"):null;
			required_runs_overs=json.has("required_runs_overs")?json.getString("required_runs_overs"):null;
			season=json.has("season")?json.getString("season"):null;
			delayed=json.has("delayed")?json.getString("delayed"):null;
			//next_ball=json.has("next_ball")?new Ball(json.getJSONObject("next_ball")):null;
			start_date_iso=json.has("start_date_iso")?json.getString("start_date_iso"):null;
			datastatus=json.has("datastatus")?json.getString("datastatus"):null;
			start_date_str=json.has("start_date_str")?json.getString("start_date_str"):null;
			key=json.has("key")?json.getString("key"):null;
			delta=json.has("delta")?json.getString("delta"):null;
			date=json.has("date")?json.getString("date"):null;
			welcome_msg=json.has("welcome_msg")?json.getString("welcome_msg"):null;
			match_comments=json.has("match_comments")?json.getString("match_comments"):null;
			pitch_report=json.has("pitch_report")?json.getString("pitch_report"):null;
			//_prev_ball=json.has("_prev_ball")?new Ball(json.getJSONObject("_prev_ball")):null;
			required_runs=json.has("required_runs")?json.getString("required_runs"):null;
			sub_card_name=json.has("sub_card_name")?json.getString("sub_card_name"):null;
			batting_team=json.has("batting_team")?json.getString("batting_team"):null;
			name=json.has("name")?json.getString("name"):null;
			title=json.has("title")?json.getString("title"):null;
			venue=json.has("venue")?json.getString("venue"):null;
			//last_ball=json.has("last_ball")?new Ball(json.getJSONObject("last_ball")):null;
			season_name=json.has("season_name")?json.getString("season_name"):null;
			first_batting=json.has("first_batting")?json.getString("first_batting"):null;
			card_name=json.has("card_name")?json.getString("card_name"):null;
			delta_str=json.has("delta_str")?json.getString("delta_str"):null;
			break_duration=json.has("break_duration")?json.getString("break_duration"):null;
			completed_by=json.has("completed_by")?json.getString("completed_by"):null;
		}
		catch(Exception ex){ex.printStackTrace();}
	}
	public ArrayList<ArrayList<String>> getRecent_overs() {
		return recent_overs;
	}
	public String getMatch_type() {
		return match_type;
	}
	public String getToss_asked_for() {
		return toss_asked_for;
	}
//	public ArrayList<ArrayList<String>> getBatting_order() {
//		return batting_order;
//	}
	public String getBreak_for() {
		return break_for;
	}
	public String getWinner_team() {
		return winner_team;
	}
	public String getStart_date_small_str() {
		return start_date_small_str;
	}
	public String getTeam_a_captainkeeper() {
		return team_a_captainkeeper;
	}
	public Innings getPlayed_innings() {
		return played_innings;
	}
	public String getToss_msg() {
		return toss_msg;
	}
	public String get_last_innings() {
		return _last_innings;
	}
	public String getMatch_type_name() {
		return match_type_name;
	}
	public String getSkey() {
		return skey;
	}
	public String getToss_won_by() {
		return toss_won_by;
	}
	public ArrayList<String> getTeam_a_playing() {
		return team_a_playing;
	}
	public String getThankyou_msg() {
		return thankyou_msg;
	}
	public String getCurrent_innings() {
		return current_innings;
	}
	public String getMatch_day() {
		return match_day;
	}
	public String getTeam_b_captainkeeper() {
		return team_b_captainkeeper;
	}
	public String getTeam_a() {
		return team_a;
	}
	public String getTeam_b() {
		return team_b;
	}
	public String getStart_date() {
		return start_date;
	}
	public String getStatus() {
		return status;
	}
	public ArrayList<String> getTeam_b_playing() {
		return team_b_playing;
	}
	public String getMan_of_match() {
		return man_of_match;
	}
	public String getRequired_runs_overs() {
		return required_runs_overs;
	}
	public String getSeason() {
		return season;
	}
	public String getDelayed() {
		return delayed;
	}
	public Ball getNext_ball() {
		return next_ball;
	}
	public String getStart_date_iso() {
		return start_date_iso;
	}
	public String getDatastatus() {
		return datastatus;
	}
	public String getStart_date_str() {
		return start_date_str;
	}
	public String getKey() {
		return key;
	}
	public String getDelta() {
		return delta;
	}
	public String getDate() {
		return date;
	}
	public String getWelcome_msg() {
		return welcome_msg;
	}
	public String getMatch_comments() {
		return match_comments;
	}
	public String getPitch_report() {
		return pitch_report;
	}
	public Ball get_prev_ball() {
		return _prev_ball;
	}
	public String getRequired_runs() {
		return required_runs;
	}
	public String getSub_card_name() {
		return sub_card_name;
	}
	public String getBatting_team() {
		return batting_team;
	}
	public String getName() {
		return name;
	}
	public String getTitle() {
		return title;
	}
	public String getVenue() {
		return venue;
	}
	public Ball getLast_ball() {
		return last_ball;
	}
	public String getSeason_name() {
		return season_name;
	}
	public String getFirst_batting() {
		return first_batting;
	}
	public String getCard_name() {
		return card_name;
	}
	public String getDelta_str() {
		return delta_str;
	}
	public String getBreak_duration() {
		return break_duration;
	}
	public String getCompleted_by() {
		return completed_by;
	}
	public JSONObject getJson() {
		return json;
	}
	
	
}
