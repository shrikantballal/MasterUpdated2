package com.snaplion.scorecard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.snaplion.kingsxi.R;
import com.snaplion.scorecard.SCHomeFragment.DownloadDataAsyncTask.MatchesHolder;
import com.snaplion.scorecard.entities.AllMatches;
import com.snaplion.scorecard.entities.Match;

public class SCHomeFragment extends Fragment 
{
	ListView list;
	View rootLayout;
	MatchesSectionedListAdapter adapter;
	private boolean currentMatchExist=false;
	private boolean pastMatchExist=false;
	private boolean comingMatchExist=false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootLayout=inflater.inflate(R.layout.sc_home_fragment, null, false);
		TextView topBarTitle = (TextView)getActivity().findViewById(R.id.v_sub_name_txt);
		topBarTitle.setText("KINGS XI PUNJAB FIXTURES");
		
		Button backBtn = (Button)getActivity().findViewById(R.id.v_sub_back_btn);
		backBtn.setBackgroundResource(R.drawable.top_bar_logo);
		backBtn.setClickable(false);
		
		LinearLayout bottomLayout = (LinearLayout)getActivity().findViewById(R.id.scorecard_bottom_bar);
		bottomLayout.setVisibility(View.VISIBLE);
		
		list = (ListView)rootLayout.findViewById(R.id.matchesList_ListView);
		list.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) 
			{
				System.out.println("getItemViewType:"+adapter.getItemViewType(position));
				System.out.println("getItem:"+adapter.getItem(position));
				System.out.println("getViewTypeCount : "+adapter.getViewTypeCount());
				if(currentMatchExist && pastMatchExist)
				{
					if(adapter.getItemViewType(position)==1)
					{
						clickedOnCurrentMatch(position);
					}
					else if(adapter.getItemViewType(position)==2)
					{
						clickedOnPastMatch(position);
					}
				}
				else if(currentMatchExist && !pastMatchExist)
				{
					if(adapter.getItemViewType(position)==1)
					{
						clickedOnCurrentMatch(position);
					}
				}
				else if(pastMatchExist && !currentMatchExist)
				{
					if(adapter.getItemViewType(position)==1)
					{
						clickedOnPastMatch(position);
					}
				}	
			}
		});
		Button refreshBtn = (Button)getActivity().findViewById(R.id.v_home_btn);
		refreshBtn.setVisibility(Button.INVISIBLE);
//		refreshBtn.setOnClickListener(new OnClickListener() 
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				new DownloadDataAsyncTask(getActivity()).execute();
//			}
//		});
		adapter = new MatchesSectionedListAdapter(getActivity());
		ProgressDialog progressDialog=new ProgressDialog(getActivity());
		progressDialog.setMessage(getActivity().getResources().getString(R.string.splash_loading_msg));
		new DownloadDataAsyncTask(getActivity(),progressDialog,true).execute();
		return rootLayout;
	}
	private void clickedOnPastMatch(int position)
	{
		Match match =(Match) adapter.getItem(position);
		String matchKey = match.getInfo().getSkey();
		FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
		ScoreCardFragment fragment=(ScoreCardFragment)fragmentManager.findFragmentByTag("ScoreCardFragment");
		if(fragment==null)
		{
			fragment=new ScoreCardFragment();
		}
		Bundle bundle=new Bundle();
		bundle.putString("MatchKey", matchKey);
		bundle.putInt("FLAG", 0);
		fragment.setArguments(bundle);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.scHomeFragment_FrameLayout, fragment, "ScoreCardFragment");
		fragmentTransaction.addToBackStack("ScoreCardFragment");
		fragmentTransaction.commit();
	}
	private void clickedOnCurrentMatch(int position)
	{
		Match match =(Match) adapter.getItem(position);
		String matchKey = match.getInfo().getSkey();
		FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
		ScoreCardFragment fragment=(ScoreCardFragment)fragmentManager.findFragmentByTag("ScoreCardFragment");
		if(fragment==null)
		{
			fragment=new ScoreCardFragment();
		}
		Bundle bundle=new Bundle();
		bundle.putString("MatchKey", matchKey);
		bundle.putInt("FLAG", 1);
		fragment.setArguments(bundle);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.scHomeFragment_FrameLayout, fragment, "ScoreCardFragment");
		fragmentTransaction.addToBackStack("ScoreCardFragment");
		fragmentTransaction.commit();
	}
	class DownloadDataAsyncTask extends AsyncTask<Void, Void, MatchesHolder>
	{
		Activity context;
		ProgressDialog progress;
		boolean showProgressFlag;
		public DownloadDataAsyncTask(Activity context,ProgressDialog progress, boolean showProgressFlag)
		{
			this.context=context;
			this.progress=progress;
			this.showProgressFlag=showProgressFlag;
		}
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			if(showProgressFlag)
			{
				progress.show();
			}
		}
		@Override
		protected MatchesHolder doInBackground(Void... params) 
		{
			AllMatches allMatches = ScorecardManager.getInstance().getAllMatchesByTeam();
			if(allMatches==null)
				return null;
			ArrayList<Match> matchesList = allMatches.getMatches().getMatches();
			MatchesHolder matchesHolder=new MatchesHolder();
			matchesHolder.currentMatches=new ArrayList<Match>();
			matchesHolder.pastMatches=new ArrayList<Match>();
			matchesHolder.upcomingMatches=new ArrayList<Match>();
			Iterator<Match> itr = matchesList.iterator();
			while(itr.hasNext())
			{
				Match match = itr.next();
				if(match.getInfo().getStatus().equalsIgnoreCase("completed"))
				{//past
					matchesHolder.pastMatches.add(match);
				}
				else if(match.getInfo().getStatus().equalsIgnoreCase("started"))
				{//live
					matchesHolder.currentMatches.add(match);
				}
				else
				{//upcoming
					matchesHolder.upcomingMatches.add(match);
				}
			}
			System.out.println(allMatches);
			return matchesHolder;
		}
		
		@Override
		protected void onPostExecute(MatchesHolder matchesHolder) 
		{
			super.onPostExecute(matchesHolder);
			if(matchesHolder!=null)
			{
				if(matchesHolder.currentMatches!=null && matchesHolder.currentMatches.size()>0)
				{
					adapter.addSection("CURRENT MATCH", new CurrentMatchesAdapter(context, matchesHolder.currentMatches));
					currentMatchExist=true;
				}
				else
				{
					currentMatchExist=false;
				}
				
				if(matchesHolder.pastMatches!=null && matchesHolder.pastMatches.size()>0)
				{
					// shrikant
					Collections.sort(matchesHolder.pastMatches);
					// end
					adapter.addSection("PAST MATCHES", new PastMatchesAdapter(context, matchesHolder.pastMatches));
					pastMatchExist=true;
				}
				else
				{
					pastMatchExist=false;
				}
				
				if(matchesHolder.upcomingMatches!=null && matchesHolder.upcomingMatches.size()>0)
				{
					Collections.sort(matchesHolder.upcomingMatches);
					adapter.addSection("UPCOMING MATCHES", new UpcomingMatchesAdapter(context, matchesHolder.upcomingMatches));
					comingMatchExist=true;
				}
				else
				{
					comingMatchExist=false;
				}
				
				list.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
			if(showProgressFlag)
			{
				progress.dismiss();
			}
		}
		
		class MatchesHolder
		{
			ArrayList<Match> currentMatches;
			ArrayList<Match> pastMatches;
			ArrayList<Match> upcomingMatches;
		}
	}
}

class UpcomingMatchesAdapter extends BaseAdapter
{
	ArrayList<Match> list;
	Activity context;
	public UpcomingMatchesAdapter(Activity context, ArrayList<Match> list)
	{
		this.list=list;
		this.context=context;
	}
	class ViewHolder
	{
		ImageView teamA_ImageView;
		ImageView teamB_ImageView;
		TextView matchName_TextView;
		TextView currentMatchStatus_TextView;
	}
	@Override
	public Match getItem(int position) 
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
			convertView =  context.getLayoutInflater().inflate(R.layout.matches_list_coming_match_item, null);
			convertView.setTag(viewHolder);
			
			viewHolder.teamA_ImageView=(ImageView)convertView.findViewById(R.id.teamA_ImageView);
			viewHolder.teamB_ImageView=(ImageView)convertView.findViewById(R.id.teamB_ImageView);
			viewHolder.matchName_TextView=(TextView)convertView.findViewById(R.id.matchName_TextView);
			viewHolder.currentMatchStatus_TextView=(TextView)convertView.findViewById(R.id.currentMatchStatus_TextView);
		}
		else
		{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		Match match=getItem(position);
		viewHolder.teamA_ImageView.setImageResource(ScorecardManager.getInstance().getTeamLogoName(match.getTeam_a().getCard_name()));
		viewHolder.teamB_ImageView.setImageResource(ScorecardManager.getInstance().getTeamLogoName(match.getTeam_b().getCard_name()));
		viewHolder.matchName_TextView.setText(match.getInfo().getSub_card_name());
		viewHolder.currentMatchStatus_TextView.setText(match.getInfo().getVenue()+", "+ScorecardManager.getInstance().getDate(match.getInfo().getStart_date()));
		return convertView;
	}
	@Override
	public long getItemId(int position) 
	{
		return position;
	}
}


class PastMatchesAdapter extends BaseAdapter
{
	ArrayList<Match> list;
	Activity context;
	public PastMatchesAdapter(Activity context, ArrayList<Match> list)
	{
		this.list=list;
		this.context=context;
	}
	class ViewHolder
	{
		ImageView teamA_ImageView;
		ImageView teamB_ImageView;
		TextView matchName_TextView;
		TextView matchResult_TextView;
		TextView currentMatchStatus_TextView;
	}
	@Override
	public Match getItem(int position) 
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
			convertView =  context.getLayoutInflater().inflate(R.layout.matches_list_past_match_item, null);
			convertView.setTag(viewHolder);
			
			viewHolder.teamA_ImageView=(ImageView)convertView.findViewById(R.id.teamA_ImageView);
			viewHolder.teamB_ImageView=(ImageView)convertView.findViewById(R.id.teamB_ImageView);
			viewHolder.matchName_TextView=(TextView)convertView.findViewById(R.id.matchName_TextView);
			viewHolder.matchResult_TextView=(TextView)convertView.findViewById(R.id.matchResult_TextView);
			viewHolder.currentMatchStatus_TextView=(TextView)convertView.findViewById(R.id.currentMatchStatus_TextView);
		}
		else
		{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		Match match=getItem(position);
		viewHolder.teamA_ImageView.setImageResource(ScorecardManager.getInstance().getTeamLogoName(match.getTeam_a().getCard_name()));
		viewHolder.teamB_ImageView.setImageResource(ScorecardManager.getInstance().getTeamLogoName(match.getTeam_b().getCard_name()));
		viewHolder.matchName_TextView.setText(match.getInfo().getSub_card_name());
		viewHolder.matchResult_TextView.setText(match.getInfo().getThankyou_msg());
		viewHolder.currentMatchStatus_TextView.setText(match.getInfo().getVenue()+", "+ScorecardManager.getInstance().getDate(match.getInfo().getStart_date()));
		return convertView;
	}
	@Override
	public long getItemId(int position) 
	{
		return position;
	}
}
class CurrentMatchesAdapter extends BaseAdapter
{
	ArrayList<Match> list;
	Activity context;
	public CurrentMatchesAdapter(Activity context, ArrayList<Match> list)
	{
		this.list=list;
		this.context=context;
	}
	class ViewHolder
	{
		ImageView teamA_ImageView;
		ImageView teamB_ImageView;
		TextView matchName_TextView;
		TextView matchStatus_TextView;
		TextView currScore_TextView;
		TextView runRate_TextView;
		TextView currentMatchStatus_TextView;
	}
	@Override
	public Match getItem(int position) 
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
			convertView =  context.getLayoutInflater().inflate(R.layout.matches_list_current_match_item, null);
			convertView.setTag(viewHolder);
			
			viewHolder.teamA_ImageView=(ImageView)convertView.findViewById(R.id.teamA_ImageView);
			viewHolder.teamB_ImageView=(ImageView)convertView.findViewById(R.id.teamB_ImageView);
			viewHolder.matchName_TextView=(TextView)convertView.findViewById(R.id.matchName_TextView);
			viewHolder.matchStatus_TextView=(TextView)convertView.findViewById(R.id.matchStatus_TextView);
			viewHolder.currScore_TextView=(TextView)convertView.findViewById(R.id.currScore_TextView);
			viewHolder.runRate_TextView=(TextView)convertView.findViewById(R.id.runRate_TextView);
			viewHolder.currentMatchStatus_TextView=(TextView)convertView.findViewById(R.id.currentMatchStatus_TextView);
		}
		else
		{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		Match match=getItem(position);
		viewHolder.teamA_ImageView.setImageResource(ScorecardManager.getInstance().getTeamLogoName(match.getTeam_a().getCard_name()));
		viewHolder.teamB_ImageView.setImageResource(ScorecardManager.getInstance().getTeamLogoName(match.getTeam_b().getCard_name()));
		viewHolder.matchName_TextView.setText(match.getInfo().getSub_card_name());
		if(match.getBat_team_name().equalsIgnoreCase("a"))
		{
			viewHolder.currScore_TextView.setText(match.getTeam_a().getRuns()+" / "+match.getTeam_a().getWickets());
			viewHolder.runRate_TextView.setText("("+Integer.parseInt(match.getTeam_a().getBalls())/6+"."+Integer.parseInt(match.getTeam_a().getBalls())%6+")");
			viewHolder.currentMatchStatus_TextView.setText(match.getTeam_a().getName()+" is playing on "+match.getTeam_a().getRun_string());
		}
		else
		{
			viewHolder.currScore_TextView.setText(match.getTeam_b().getRuns()+" / "+match.getTeam_b().getWickets());
			viewHolder.runRate_TextView.setText("("+Integer.parseInt(match.getTeam_b().getBalls())/6+"."+Integer.parseInt(match.getTeam_b().getBalls())%6+")");
			viewHolder.currentMatchStatus_TextView.setText(match.getTeam_b().getName()+" is playing on "+match.getTeam_b().getRun_string());
		}
		
		
		return convertView;
	}
	@Override
	public long getItemId(int position) 
	{
		return position;
	}
}
class SectionAdapter extends BaseAdapter
{
	Activity context;
	public SectionAdapter(Activity context)
	{
		this.context=context;
	}
	ArrayList<String> list=new ArrayList<String>();
	public void add(String section)
	{
		list.add(section);
	}
	@Override
	public int getCount() 
	{
		return list.size();
	}

	@Override
	public String getItem(int position) 
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
		TextView sectionName_TextView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if(convertView==null)
		{
			convertView =  context.getLayoutInflater().inflate(R.layout.matches_list_section_divider, null);
			viewHolder=new ViewHolder();
			viewHolder.sectionName_TextView=(TextView)convertView.findViewById(R.id.sectionName_TextView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		viewHolder.sectionName_TextView.setText(getItem(position));
		return convertView;
	}
	
}
class MatchesSectionedListAdapter extends BaseAdapter 
{
	
	public final Map<String,Adapter> sections = new LinkedHashMap<String,Adapter>();
	public final SectionAdapter headers;
	public final static int TYPE_SECTION_HEADER = 0;
	
	public MatchesSectionedListAdapter(Activity context) 
	{
		headers = new SectionAdapter(context);
		//headers = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
	}
	
	public void addSection(String section, Adapter adapter) {
		this.headers.add(section);
		this.sections.put(section, adapter);
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
			if(position == 0) 
				return TYPE_SECTION_HEADER;
			if(position < size) 
				return type + adapter.getItemViewType(position - 1);
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
