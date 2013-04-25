package com.snaplion.fanwall.wall;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import com.codinglines.lib.dynamiclist.AdapterItem;
import com.codinglines.lib.dynamiclist.DynamicViewAdapter;
import com.codinglines.lib.dynamiclist.ViewHolder;
import com.snaplion.fanwall.FanwallComment;
import com.snaplion.kingsxi.R;

public class TestDynamicViewAdapter extends DynamicViewAdapter
{
	public final TestDynamicModel _model;
	FanwallWallActivity activity;
	String catName;
	public Handler mHandler;
	public TestDynamicViewAdapter(FanwallWallActivity activity, TestDynamicModel model, String catName)
	{
		super(activity, R.layout.progress_item, R.layout.unavailable_item,R.layout.empty_item);
		this.activity = activity;
		this.catName = catName;
		_model = model;
		mHandler=new Handler()
		{
			@Override
			public void dispatchMessage(Message msg)
			{
				setDataCompleted();
			}
		};
		_model.setLoadCompleted(mHandler);
		submitDataRequest();
	}

	@Override
	public View onDataView(int position, AdapterItem item, View convertView, ViewGroup parent)
	{
		convertView = tryInflateView(convertView, ItemViewHolder.ResourceId,parent);
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		if (viewHolder == null)
		{
			viewHolder = new ItemViewHolder(convertView, activity, catName);
		}
		viewHolder.render(item);
		return convertView;
	}


	/*This method should return true if there is more data to load. 
	 * For instance it loads by 20 items, for list with 60 items it 
	 * should return true until it riches (60th) last item in the list;
	 * */
	@Override
	protected boolean isMoreToLoad()
	{
		//return _model.getCount() < 101;
		return _model.loadMoreItem;
	}
	
	/*This method is called when data item needs to be loaded from the model to the adapter; 
	 * Use addItem method to load every item from the model.*(non-Javadoc)
	 * @see com.examples.dynamiclist.core.DynamicViewAdapter#isMoreToLoad()
	 */
	@Override
	protected void onDataLoad()
	{
		for (int i = 0; i < _model.getCount(); i++)
		{
			FanwallComment comment = _model.getItem(i);
			addItem(comment, comment.getIndex());
		}
	}

	@Override
	protected void onSubmitDataRequest()
	{
		_model.loadMore(false);
	}
	
	public TestDynamicModel getModel()
	{
		return _model;
	}
	/////////////////////////
	public void reloadDataInAdapter()
	{
		_model.loadMore(true);
		mHandler.sendEmptyMessage(0);
	}
	public void reIndex()
	{
		for(int i=0;i<_model.postArrayList.size();i++)
		{
			_model.postArrayList.get(i).setIndex(i);
		}
	}
}
