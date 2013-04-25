package com.snaplion.scorecard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaplion.kingsxi.R;
import com.snaplion.myapp.SnapLionMyAppActivity;
import com.snaplion.scorecard.entities.Match;
import com.snaplion.scorecard.entities.Player;
import com.snaplion.scorecard.entities.Team;

public class ScoreCardFragment extends Fragment
{
	private final static int TEAM_A=100;
	private final static int TEAM_B=101;
	private final static int BATTING=102;
	private final static int BOWLING=103;
	
	int selectedTeamKey=TEAM_A;
	int selectedInningKey=BATTING;
	View rootLayout;
	ListView list;
	TextView teamName_TextView;
	TextView runs_TextView;
	TextView overs_TextView;
	TextView runrate_TextView;
	TextView vaneu_TextView;
	RelativeLayout TeamALogoBG_RelativeLayout;
	RelativeLayout TeamBLogoBG_RelativeLayout;
	ImageView TeamALogo_ImageView;
	ImageView TeamBLogo_ImageView;
	Button batting_Button;
	Button bowling_Button;
	BattingCardSectionedListAdapter adapter;
	private Match matchObject;
	int MATCH_FLAG;
	String matchKey=null;
	
	SCRefreshBGBroadcastReceiver alarm;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootLayout=inflater.inflate(R.layout.sc_batting_fragment, null, false);
		TextView topBarTitle = (TextView)getActivity().findViewById(R.id.v_sub_name_txt);
		topBarTitle.setText("KINGS XI PUNJAB SCORECARD");
		
		Button backBtn = (Button)getActivity().findViewById(R.id.v_sub_back_btn);
		backBtn.setClickable(true);
		backBtn.setBackgroundResource(R.drawable.selector_back_btn);
		backBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				SnapLionMyAppActivity.KillFlag = false;
				getActivity().onBackPressed();
			}
		});
		
		LinearLayout bottomLayout = (LinearLayout)getActivity().findViewById(R.id.scorecard_bottom_bar);
		bottomLayout.setVisibility(View.GONE);
		
		Bundle bundle = getArguments();
		matchKey = bundle.getString("MatchKey");
		MATCH_FLAG = bundle.getInt("FLAG");
		if(MATCH_FLAG==1)
			selectedInningKey=BATTING;
		teamName_TextView = (TextView)rootLayout.findViewById(R.id.teamName_TextView);
		runs_TextView = (TextView)rootLayout.findViewById(R.id.runs_TextView);
		overs_TextView = (TextView)rootLayout.findViewById(R.id.overs_TextView);
		runrate_TextView = (TextView)rootLayout.findViewById(R.id.runrate_TextView);
		vaneu_TextView = (TextView)rootLayout.findViewById(R.id.vaneu_TextView);
		TeamALogoBG_RelativeLayout = (RelativeLayout)rootLayout.findViewById(R.id.TeamALogoBG_RelativeLayout);
		TeamBLogoBG_RelativeLayout = (RelativeLayout)rootLayout.findViewById(R.id.TeamBLogoBG_RelativeLayout);
		TeamALogo_ImageView = (ImageView)rootLayout.findViewById(R.id.TeamALogo_ImageView);
		TeamBLogo_ImageView = (ImageView)rootLayout.findViewById(R.id.TeamBLogo_ImageView);
		batting_Button = (Button)rootLayout.findViewById(R.id.batting_Button);
		bowling_Button = (Button)rootLayout.findViewById(R.id.bowling_Button);
		list = (ListView)rootLayout.findViewById(R.id.scoreList_ListView);
		
		batting_Button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				batting_Button.setBackgroundResource(R.drawable.batting_sel);
				bowling_Button.setBackgroundResource(R.drawable.bowling_unsel);
				selectedInningKey=BATTING;
				initUI();
			}
		});
		bowling_Button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				bowling_Button.setBackgroundResource(R.drawable.bowling_sel);
				batting_Button.setBackgroundResource(R.drawable.batting_unsel);
				selectedInningKey=BOWLING;
				initUI();
			}
		});
		TeamALogoBG_RelativeLayout.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				TeamALogoBG_RelativeLayout.setBackgroundResource(R.drawable.team_logo_bg_sel);
				TeamBLogoBG_RelativeLayout.setBackgroundResource(R.drawable.team_logo_bg_unsel);
				selectedTeamKey=TEAM_A;
				initUI();
			}
		});
		TeamBLogoBG_RelativeLayout.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				TeamALogoBG_RelativeLayout.setBackgroundResource(R.drawable.team_logo_bg_unsel);
				TeamBLogoBG_RelativeLayout.setBackgroundResource(R.drawable.team_logo_bg_sel);
				selectedTeamKey=TEAM_B;
				initUI();
			}
		});
		
		Button refreshBtn = (Button)getActivity().findViewById(R.id.v_home_btn);
		if(MATCH_FLAG==1)
		{
			refreshBtn.setVisibility(Button.VISIBLE);
			refreshBtn.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					ProgressDialog progressDialog=new ProgressDialog(getActivity());
					progressDialog.setMessage(getActivity().getResources().getString(R.string.splash_loading_msg));
					new DownloadDataAsyncTask(getActivity(),matchKey,false,progressDialog,true).execute();
				}
			});
		}
		else
		{
			refreshBtn.setVisibility(Button.INVISIBLE);
		}
		
		adapter = new BattingCardSectionedListAdapter(getActivity());
		ProgressDialog progressDialog=new ProgressDialog(getActivity());
		progressDialog.setMessage(getActivity().getResources().getString(R.string.splash_loading_msg));
		
		new DownloadDataAsyncTask(getActivity(),matchKey,true,progressDialog,true).execute();
		
		if(MATCH_FLAG==1)
		{
			//new DownloadDataAsyncTask(getActivity(),matchKey,false,null,false).execute();
			alarm = new SCRefreshBGBroadcastReceiver();
	        IntentFilter filter = new IntentFilter();
	    	filter.addAction("REFRESH_SCORE_CARD_BG_PROCESS");
	    	getActivity().registerReceiver(alarm, filter);
	    	alarm.setAlarm(getActivity());
		}
		
		return rootLayout;
	}
	private void initUI()
	{
		if(matchObject==null)
			return;
		
		Team team;
		if(selectedTeamKey==TEAM_B)
		{
			team=matchObject.getTeam_b();
			TeamALogoBG_RelativeLayout.setBackgroundResource(R.drawable.team_logo_bg_unsel);
			TeamBLogoBG_RelativeLayout.setBackgroundResource(R.drawable.team_logo_bg_sel);
		}
		else
		{
			team=matchObject.getTeam_a();
			TeamALogoBG_RelativeLayout.setBackgroundResource(R.drawable.team_logo_bg_sel);
			TeamBLogoBG_RelativeLayout.setBackgroundResource(R.drawable.team_logo_bg_unsel);
		}
		teamName_TextView.setText(team.getName());
		runs_TextView.setText(team.getRuns()+" / "+team.getWickets()+" (20.0)");
		overs_TextView.setText("Overs: "+team.getInnings().getFirst().getOvers());
		runrate_TextView.setText("RR: "+team.getInnings().getFirst().getRun_rate());
		
		if(selectedInningKey==BATTING)
		{
			adapter.removeAllSections();
			String[] headers={"R","B","4s","6s","S/R"}; 
			adapter.addSection(new Section("Batting", headers), new PlayersBattingAdapter(getActivity(), team.getInnings().getFirst().getPlayers().getPlayerByBattingOrder()));
			adapter.addSection(new Section("Fall of Wickets", team), new PlayersBattingFallOfWicketAdapter(getActivity(), team.getInnings().getFirst().getFall_of_wicket()));
		}
		else
		{
			adapter.removeAllSections();
			ArrayList<Player> BowledPlayerList=new ArrayList<Player>();
//			for(Player plr : team.getInnings().getFirst().getPlayers().getPlayers())
//			{
//				if(plr.getBowling().getBalls()!=null)
//					BowledPlayerList.add(plr);
//			}
			String[] headers={"O","B","R","W","E/R"}; 
			adapter.addSection(new Section("Bowling", headers), new PlayersBowlingAdapter(getActivity(), team.getInnings().getFirst().getPlayers().getPlayerByBowlingOrder()));
		}
		
		list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	private void refreshScoreCard()
	{
		new Handler().postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				new DownloadDataAsyncTask(getActivity(),matchKey,false,null,false).execute();
			}
		}, 30*1000);
	}
	class DownloadDataAsyncTask extends AsyncTask<Void, Void, Match>
	{
		Context context;
		String matchKey;
		boolean first;
		ProgressDialog progress;
		boolean showProgress;
		public DownloadDataAsyncTask(Context context,String matchKey,boolean first, ProgressDialog progress, boolean showProgress)
		{
			this.context=context;
			this.matchKey=matchKey;
			this.first=first;
			this.progress=progress;
			this.showProgress=showProgress;
		}
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			if(showProgress)
			{
				progress.show();
			}
		}
		@Override
		protected Match doInBackground(Void... params) 
		{
			Match match = ScorecardManager.getInstance().getMatche(matchKey);
			return match;
		}
		@Override
		protected void onPostExecute(Match match) 
		{
			super.onPostExecute(match);
			if(match!=null && match.getTeam_a()!=null && match.getTeam_b()!=null)
			{
				matchObject=match;
				TeamALogo_ImageView.setImageResource(ScorecardManager.getInstance().getTeamLogoName(matchObject.getTeam_a().getCard_name()));
				TeamBLogo_ImageView.setImageResource(ScorecardManager.getInstance().getTeamLogoName(matchObject.getTeam_b().getCard_name()));
				
				vaneu_TextView.setText(matchObject.getInfo().getVenue());
				
				if(MATCH_FLAG==1 && first && matchObject.getBat_team_name()!=null)
				{
					String team_char = matchObject.getBat_team_name();
					Log.d("MTK","bat_team_name:"+team_char);
					
					if(team_char.equalsIgnoreCase("b"))
					{
						selectedTeamKey=TEAM_B;
					}
					else
					{
						selectedTeamKey=TEAM_A;
					}
				}
				initUI();
			}
			if(showProgress)
			{
				progress.dismiss();
			}
//			if(MATCH_FLAG==1 && !showProgress)
//				refreshScoreCard();
		}
	}
	@Override
	public void onDestroyView() 
	{
		super.onDestroyView();
		if(MATCH_FLAG==1)
		{
			alarm.cancelAlarm(getActivity());
			getActivity().unregisterReceiver(alarm);
		}
	}
	///////////////////////////////////////////
	class SCRefreshBGBroadcastReceiver extends BroadcastReceiver 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			 PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
	         wl.acquire();
	         System.out.println("onReceive");
	         new DownloadDataAsyncTask(getActivity(),matchKey,false,null,false).execute();
	         wl.release();
	    }
		public void setAlarm(Context context)
	    {
	        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	        Intent intent = new Intent("REFRESH_SCORE_CARD_BG_PROCESS");
	        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 30 , pi);
	    }
	    public void cancelAlarm(Context context)
	    {
	        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
	        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
	        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	        alarmManager.cancel(sender);
	    }
	}
}
class Section
{
	public Section(String sectionTitle, Object sectionObject)
	{
		this.sectionTitle=sectionTitle;
		this.sectionObject=sectionObject;
	}
	private String sectionTitle;
	private Object sectionObject;
	
	public String getSectionTitle() {
		return sectionTitle;
	}
	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}
	public Object getSectionObject() {
		return sectionObject;
	}
	public void setSectionObject(Object sectionObject) {
		this.sectionObject = sectionObject;
	}
}

class PlayersBowlingAdapter extends BaseAdapter
{
	ArrayList<Player> list;
	Activity context;
	public PlayersBowlingAdapter(Activity context, ArrayList<Player> list)
	{
		this.list=list;
		this.context=context;
	}
	class ViewHolder
	{
		TextView playerName_TextView;
		TextView playerOvers_TextView;
		TextView playerRun_TextView;
		TextView playerWicket_TextView;
		TextView playerBalls_TextView;
		TextView playerEconomyRate_TextView;
	}
	@Override
	public Player getItem(int position) 
	{
		return list.get(position);
	}
	@Override
	public int getCount() 
	{
		return list.size();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if(convertView==null)
		{
			viewHolder=new ViewHolder();
			convertView =  context.getLayoutInflater().inflate(R.layout.bowling_card_players_item, null);
			convertView.setTag(viewHolder);
			
			viewHolder.playerName_TextView=(TextView)convertView.findViewById(R.id.playerName_TextView);
			viewHolder.playerOvers_TextView=(TextView)convertView.findViewById(R.id.playerOvers_TextView);
			viewHolder.playerRun_TextView=(TextView)convertView.findViewById(R.id.playerRun_TextView);
			viewHolder.playerWicket_TextView=(TextView)convertView.findViewById(R.id.playerWicket_TextView);
			viewHolder.playerBalls_TextView=(TextView)convertView.findViewById(R.id.playerBalls_TextView);
			viewHolder.playerEconomyRate_TextView=(TextView)convertView.findViewById(R.id.playerEconomyRate_TextView);
		}
		else
		{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		Player player=getItem(position);
		viewHolder.playerName_TextView.setText(player.getName());
		viewHolder.playerOvers_TextView.setText(player.getBowling().getOvers());
		viewHolder.playerRun_TextView.setText(player.getBowling().getRuns());
		viewHolder.playerWicket_TextView.setText(player.getBowling().getWicket());
		viewHolder.playerBalls_TextView.setText(player.getBowling().getBalls());
		float SR = (Float.parseFloat(player.getBowling().getRuns())/Float.parseFloat(player.getBowling().getOvers()));
		viewHolder.playerEconomyRate_TextView.setText(String.format("%.02f", SR));
		return convertView;
	}
	@Override
	public long getItemId(int position) 
	{
		return position;
	}
}
class PlayersBattingAdapter extends BaseAdapter
{
	ArrayList<Player> list;
	Activity context;
	public PlayersBattingAdapter(Activity context, ArrayList<Player> list)
	{
		this.list=list;
		this.context=context;
	}
	class ViewHolder
	{
		TextView playerName_TextView;
		TextView playerStatus_TextView;
		TextView playerRun_TextView;
		TextView playerBall_TextView;
		TextView playerFours_TextView;
		TextView playerSixes_TextView;
		TextView playerStrikeRate_TextView;
	}
	@Override
	public Player getItem(int position) 
	{
		return list.get(position);
	}
	@Override
	public int getCount() 
	{
		return list.size();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		try
		{
			ViewHolder viewHolder;
			if(convertView==null)
			{
				viewHolder=new ViewHolder();
				convertView =  context.getLayoutInflater().inflate(R.layout.battings_card_players_item, null);
				convertView.setTag(viewHolder);
				
				viewHolder.playerName_TextView=(TextView)convertView.findViewById(R.id.playerName_TextView);
				viewHolder.playerStatus_TextView=(TextView)convertView.findViewById(R.id.playerStatus_TextView);
				viewHolder.playerRun_TextView=(TextView)convertView.findViewById(R.id.playerRun_TextView);
				viewHolder.playerBall_TextView=(TextView)convertView.findViewById(R.id.playerBall_TextView);
				viewHolder.playerFours_TextView=(TextView)convertView.findViewById(R.id.playerFours_TextView);
				viewHolder.playerSixes_TextView=(TextView)convertView.findViewById(R.id.playerSixes_TextView);
				viewHolder.playerStrikeRate_TextView=(TextView)convertView.findViewById(R.id.playerStrikeRate_TextView);
			}
			else
			{
				viewHolder=(ViewHolder)convertView.getTag();
			}
			Player player=getItem(position);
			viewHolder.playerName_TextView.setText(player.getName());
			viewHolder.playerStatus_TextView.setText(player.getOut_str());
			viewHolder.playerRun_TextView.setText(player.getBatting().getRuns());
			viewHolder.playerBall_TextView.setText(player.getBatting().getBalls());
			viewHolder.playerFours_TextView.setText(player.getBatting().getFour());
			viewHolder.playerSixes_TextView.setText(player.getBatting().getSix());
			float SR = (Float.parseFloat(player.getBatting().getRuns())/Float.parseFloat(player.getBatting().getBalls()))*100f;
			viewHolder.playerStrikeRate_TextView.setText(String.format("%.02f", SR));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return convertView;
	}
	@Override
	public long getItemId(int position) 
	{
		return position;
	}
}
class PlayersBattingFallOfWicketAdapter extends BaseAdapter
{
	ArrayList<String> list;
	Activity context;
	public PlayersBattingFallOfWicketAdapter(Activity context, ArrayList<String> list)
	{
		this.list=list;
		this.context=context;
	}
	class ViewHolder
	{
		TextView outAtRun_TextView;
	}
	@Override
	public String getItem(int position) 
	{
		return list.get(position);
	}
	@Override
	public int getCount() 
	{
		return list.size();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if(convertView==null)
		{
			viewHolder=new ViewHolder();
			convertView =  context.getLayoutInflater().inflate(R.layout.battings_card_fallofwicket_item, null);
			convertView.setTag(viewHolder);
			
			viewHolder.outAtRun_TextView=(TextView)convertView.findViewById(R.id.outAtRun_TextView);
		}
		else
		{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		viewHolder.outAtRun_TextView.setText(getItem(position));
		return convertView;
	}
	@Override
	public long getItemId(int position) 
	{
		return position;
	}
}

class BattingCardSectionAdapter extends BaseAdapter
{
	Activity context;
	public BattingCardSectionAdapter(Activity context)
	{
		this.context=context;
	}
	ArrayList<Section> list=new ArrayList<Section>();
	public void add(Section section)
	{
		list.add(section);
	}
	public void removeAllSection()
	{
		list.clear();
	}
	@Override
	public int getCount() 
	{
		return list.size();
	}
	
	@Override
	public Section getItem(int position) 
	{
		return list.get(position);
	}
	
	@Override
	public long getItemId(int position) 
	{
		return position;
	}
	class ViewHolder
	{
		LinearLayout extrasRoot_LinearLayout;
		TextView extras_TextView;
		TextView totalScore_TextView;
		TextView sectionName_TextView;
		
		TextView playerRun_TextView;
		TextView playerBall_TextView;
		TextView playerFours_TextView;
		TextView playerSixes_TextView;
		TextView playerStrikeRate_TextView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if(convertView==null)
		{
			convertView =  context.getLayoutInflater().inflate(R.layout.batting_card_section_divider, null);
			viewHolder=new ViewHolder();
			viewHolder.extrasRoot_LinearLayout=(LinearLayout)convertView.findViewById(R.id.extrasRoot_LinearLayout);
			viewHolder.extras_TextView=(TextView)convertView.findViewById(R.id.extras_TextView);
			viewHolder.totalScore_TextView=(TextView)convertView.findViewById(R.id.totalScore_TextView);
			viewHolder.sectionName_TextView=(TextView)convertView.findViewById(R.id.sectionName_TextView);
			viewHolder.playerRun_TextView=(TextView)convertView.findViewById(R.id.playerRun_TextView);
			viewHolder.playerBall_TextView=(TextView)convertView.findViewById(R.id.playerBall_TextView);
			viewHolder.playerFours_TextView=(TextView)convertView.findViewById(R.id.playerFours_TextView);
			viewHolder.playerSixes_TextView=(TextView)convertView.findViewById(R.id.playerSixes_TextView);
			viewHolder.playerStrikeRate_TextView=(TextView)convertView.findViewById(R.id.playerStrikeRate_TextView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		viewHolder.sectionName_TextView.setText(getItem(position).getSectionTitle());
		viewHolder.playerRun_TextView.setVisibility(TextView.INVISIBLE);
		viewHolder.playerBall_TextView.setVisibility(TextView.INVISIBLE);
		viewHolder.playerFours_TextView.setVisibility(TextView.INVISIBLE);
		viewHolder.playerSixes_TextView.setVisibility(TextView.INVISIBLE);
		viewHolder.playerStrikeRate_TextView.setVisibility(TextView.INVISIBLE);
		viewHolder.extrasRoot_LinearLayout.setVisibility(LinearLayout.GONE);
		
		if(getItem(position).getSectionTitle().equalsIgnoreCase("Fall of Wickets"))
		{
			Team team = (Team) getItem(position).getSectionObject();
			viewHolder.extrasRoot_LinearLayout.setVisibility(LinearLayout.VISIBLE);
			viewHolder.extras_TextView.setText("Extras : "+team.getExtras());
			viewHolder.totalScore_TextView.setText("Total: "+team.getRuns()+"/"+team.getWickets()+" ("+team.getInnings().getFirst().getOvers()+")");
		}
		else if(getItem(position).getSectionTitle().equalsIgnoreCase("Bowling") || getItem(position).getSectionTitle().equalsIgnoreCase("Batting"))
		{
			viewHolder.playerRun_TextView.setVisibility(TextView.VISIBLE);
			viewHolder.playerBall_TextView.setVisibility(TextView.VISIBLE);
			viewHolder.playerFours_TextView.setVisibility(TextView.VISIBLE);
			viewHolder.playerSixes_TextView.setVisibility(TextView.VISIBLE);
			viewHolder.playerStrikeRate_TextView.setVisibility(TextView.VISIBLE);
			String[] headersData=(String[]) getItem(position).getSectionObject();
			
			viewHolder.playerRun_TextView.setText(headersData[0]);
			viewHolder.playerBall_TextView.setText(headersData[1]);
			viewHolder.playerFours_TextView.setText(headersData[2]);
			viewHolder.playerSixes_TextView.setText(headersData[3]);
			viewHolder.playerStrikeRate_TextView.setText(headersData[4]);
		}
		return convertView;
	}
}
class BattingCardSectionedListAdapter extends BaseAdapter 
{
	public final Map<String,Adapter> sections = new LinkedHashMap<String,Adapter>();
	public final BattingCardSectionAdapter headers;
	public final static int TYPE_SECTION_HEADER = 0;
	
//	@Override
//	public void notifyDataSetChanged() 
//	{
//		super.notifyDataSetChanged();
//		for(Adapter adapter : this.sections.values())
//		{
//			if(adapter instanceof BattingCardSectionAdapter)
//			{
//				((BattingCardSectionAdapter)adapter).notifyDataSetChanged();
//			}
//			else if(adapter instanceof PlayersBattingFallOfWicketAdapter)
//			{
//				((PlayersBattingFallOfWicketAdapter)adapter).notifyDataSetChanged();
//			}
//			else if(adapter instanceof PlayersBattingAdapter)
//			{
//				((PlayersBattingAdapter)adapter).notifyDataSetChanged();
//			}
//			else if(adapter instanceof PlayersBowlingAdapter)
//			{
//				((PlayersBowlingAdapter)adapter).notifyDataSetChanged();
//			}
//		}
//	}
	public BattingCardSectionedListAdapter(Activity context) 
	{
		headers = new BattingCardSectionAdapter(context);
		//headers = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
	}
	
	public void addSection(Section section, Adapter adapter) {
		this.headers.add(section);
		this.sections.put(section.getSectionTitle(), adapter);
	}
	public void removeAllSections() 
	{
		this.headers.removeAllSection();
		this.sections.clear();
	}
	public Object getItem(int position) 
	{
		for(Object section : this.sections.keySet()) 
		{
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;
			// check if position inside this section 
			if(position == 0) 
				return section;
			if(position < size) 
				return adapter.getItem(position - 1);
			// otherwise jump into next section
			position -= size;
		}
		return null;
	}
	
	public int getCount() 
	{
		// total together all sections, plus one for each section header
		int total = 0;
		for(Adapter adapter : this.sections.values())
			total += adapter.getCount() + 1;
		return total;
	}
	
	public int getViewTypeCount() {
		// assume that headers count as one, then total all sections
		int total = 1;
		for(Adapter adapter : this.sections.values())
			total += adapter.getViewTypeCount();
		return total;
	}
	
	public int getItemViewType(int position) 
	{
		int type = 1;
		for(Object section : this.sections.keySet()) 
		{
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;
			// check if position inside this section 
			if(position == 0) return TYPE_SECTION_HEADER;
			if(position < size) return type + adapter.getItemViewType(position - 1);
			// otherwise jump into next section
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;
	}
	
	public boolean areAllItemsSelectable() 
	{
		return false;
	}
	
	public boolean isEnabled(int position) 
	{
		return (getItemViewType(position) != TYPE_SECTION_HEADER);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		int sectionnum = 0;
		for(Object section : this.sections.keySet()) 
		{
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;
			
			// check if position inside this section 
			if(position == 0) 
				return headers.getView(sectionnum, convertView, parent);
			if(position < size) 
				return adapter.getView(position - 1, convertView, parent);
	
			// otherwise jump into next section
			position -= size;
			sectionnum++;
		}
		return null;
	}
	@Override
	public long getItemId(int position) 
	{
		return position;
	}
}


