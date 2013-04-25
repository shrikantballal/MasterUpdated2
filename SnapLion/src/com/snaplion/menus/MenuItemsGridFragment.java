package com.snaplion.menus;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.snaplion.fanwall.FanwalLoginManager;
import com.snaplion.kingsxi.R;
import com.snaplion.util.AppManager;
import com.snaplion.util.Utility;

public class MenuItemsGridFragment extends Fragment
{
	public String topHeading;
	public ArrayList<MenusItem> menusItems=new ArrayList<MenusItem>();
	AQuery aq;
	View rootLayout;
	GridView imageGrid;
	ImageGridAdapter adapter;
	AppManager appManager;
	public boolean showBackFlag;
	public int displayIconWidth;
	
	@Override
	public void onResume() 
	{
		if(showBackFlag)
		{
			Message msg=new Message();
	    	msg.arg1=FanwalLoginManager.SHOW_BACK_BUTTON;
			((MenusActivity)getActivity()).mHandler.sendMessage(msg);
		}
		else
		{
			Message msg=new Message();
	    	msg.arg1=FanwalLoginManager.HIDE_BACK_BUTTON;
			((MenusActivity)getActivity()).mHandler.sendMessage(msg);
		}
		Message msg=new Message();
    	msg.arg1=FanwalLoginManager.SET_TOP_HEADING;
    	msg.obj=topHeading;
		((MenusActivity)getActivity()).mHandler.sendMessage(msg);
		super.onResume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootLayout=inflater.inflate(R.layout.menu_items_grid_fragment, container, false);
		//rootLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
		aq=new AQuery(getActivity());
		appManager=AppManager.getInstance();
		
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		displayIconWidth=(display.getWidth()/3)-10;
		
		imageGrid=(GridView)rootLayout.findViewById(R.id.menus_grid);
		if(menusItems.size()==0)
		{
			Toast.makeText(getActivity(), "No Content Found", Toast.LENGTH_SHORT).show();
		}
		adapter = new ImageGridAdapter(getActivity(), menusItems);
	    imageGrid.setAdapter(adapter);
		imageGrid.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				MenusItem menuItem=menusItems.get(position);
				if(menusItems!=null && menuItem.getImages()==null && menuItem.getCategaryid()==null)//image
				{
					//open gallery
					Utility.debug("open gallery");
					
					Intent intent=new Intent(getActivity(), MenusGallery.class);
					intent.putParcelableArrayListExtra("MenusArrayList", menusItems);
					intent.putExtra("CURRENT_INDEX", position);
					intent.putExtra("SECTION_OPENED_BY", "menus");
					startActivity(intent);
				}
				else //folder
				{
					((MenusActivity)getActivity()).mGaTracker.sendView("Menu_Album"+menuItem.getCategaryid());
					Utility.debug("open folder");
					MenuItemsGridFragment newFragment = new MenuItemsGridFragment();
					if(menuItem.getImages()!=null)
					{
						newFragment.menusItems=menuItem.getImages();
					}
					else
					{
						newFragment.menusItems=new ArrayList<MenusItem>();
					}
					newFragment.showBackFlag=true;
					newFragment.topHeading=menuItem.getCategoryname();
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
					ft.replace(R.id.menu_home_activity, newFragment);
					ft.addToBackStack(String.valueOf(newFragment.hashCode()));
			        ft.commit();
				}
			}
		});
		
		return rootLayout;
	}
	 
	class ImageGridAdapter extends BaseAdapter
	{
		Context context; 
		ArrayList<MenusItem> list;
		ImageGridAdapter(Context context, ArrayList<MenusItem> list)
		{
			this.context=context;
			this.list=list;
		}
		@Override
		public int getCount() 
		{
			return list.size();
		}

		@Override
		public MenusItem getItem(int position) 
		{
			return list.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			MenusItem menuItem=list.get(position);
			if(menuItem.getCategaryid()==null && menuItem.getImages()==null)//image
			{
				convertView = getActivity().getLayoutInflater().inflate(R.layout.menu_item, null);
				ProgressBar menu_image_ProgressBar = (ProgressBar)convertView.findViewById(R.id.menu_image_ProgressBar);
				ImageView menuImage = (ImageView) convertView.findViewById(R.id.menu_image);
				menuImage.setLayoutParams(new RelativeLayout.LayoutParams(displayIconWidth, displayIconWidth));
				Bitmap bitmap = aq.getCachedImage(menuItem.getSmallpicture());
		    	if(bitmap!=null)
		    	{
		    		aq.id(menuImage).image(bitmap);
		    	}
		    	else
		    	{
		    		aq.id(menuImage)
		    		.progress(menu_image_ProgressBar)
		    		.image(menuItem.getSmallpicture(), false, true);
		    	}
		    }
			else//folder
			{
				convertView = getActivity().getLayoutInflater().inflate(R.layout.menu_folder, null);
				ImageView menu_folder_img = (ImageView) convertView.findViewById(R.id.menu_folder_img);
				menu_folder_img.setLayoutParams(new RelativeLayout.LayoutParams(displayIconWidth, displayIconWidth));
				TextView menu_folder_name = (TextView) convertView.findViewById(R.id.menu_folder_name);
				ProgressBar menu_image_folder_ProgressBar = (ProgressBar)convertView.findViewById(R.id.menu_image_folder_ProgressBar);
				
				Bitmap bitmap = aq.getCachedImage(menuItem.getSmallpicture());
		    	if(bitmap!=null)
		    	{
		    		aq.id(menu_folder_img).image(bitmap);
		    	}
		    	else
		    	{
		    		aq.id(menu_folder_img)
		    		.progress(menu_image_folder_ProgressBar)
		    		.image(menuItem.getAlbumimage(), false, true);
		    	}
		    	menu_folder_name.setText(menuItem.getCategoryname());
			}
//			if(menuItem.getAlbumimage()==null && menuItem.getImages()==null)//image
//			{
//				convertView = getActivity().getLayoutInflater().inflate(R.layout.menu_item, null);
//				ProgressBar menu_image_ProgressBar = (ProgressBar)convertView.findViewById(R.id.menu_image_ProgressBar);
//				ImageView menuImage = (ImageView) convertView.findViewById(R.id.menu_image);
//				menuImage.setLayoutParams(new RelativeLayout.LayoutParams(displayIconWidth, displayIconWidth));
//				Bitmap bitmap = aq.getCachedImage(menuItem.getSmallpicture());
//		    	if(bitmap!=null)
//		    	{
//		    		aq.id(menuImage).image(bitmap);
//		    	}
//		    	else
//		    	{
//		    		aq.id(menuImage)
//		    		.progress(menu_image_ProgressBar)
//		    		.image(menuItem.getSmallpicture(), false, true);
//		    	}
//		    }
//			else//folder
//			{
//				convertView = getActivity().getLayoutInflater().inflate(R.layout.menu_folder, null);
//				ImageView menu_folder_img = (ImageView) convertView.findViewById(R.id.menu_folder_img);
//				menu_folder_img.setLayoutParams(new RelativeLayout.LayoutParams(displayIconWidth, displayIconWidth));
//				TextView menu_folder_name = (TextView) convertView.findViewById(R.id.menu_folder_name);
//				ProgressBar menu_image_folder_ProgressBar = (ProgressBar)convertView.findViewById(R.id.menu_image_folder_ProgressBar);
//				
//				Bitmap bitmap = aq.getCachedImage(menuItem.getSmallpicture());
//		    	if(bitmap!=null)
//		    	{
//		    		aq.id(menu_folder_img).image(bitmap);
//		    	}
//		    	else
//		    	{
//		    		aq.id(menu_folder_img)
//		    		.progress(menu_image_folder_ProgressBar)
//		    		.image(menuItem.getAlbumimage(), false, true);
//		    	}
//		    	menu_folder_name.setText(menuItem.getCategoryname());
//			}
			return convertView;
		}
	}
}
