package com.navigation.drawer.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidhive.dashboard.R;

public class DrawerItemCustomAdapter extends ArrayAdapter<ObjectDrawerItem> {

	Context mContext;
	int layoutResourceId;
	ObjectDrawerItem data[] = null;
	String countRec[]=null;

	/*
	 * @mContext - app context
	 * 
	 * @layoutResourceId - the listview_item_row.xml
	 * 
	 * @data - the ListItem data
	 */
	public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, ObjectDrawerItem[] data, String[] countRec) {

		super(mContext, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.mContext = mContext;
		this.data = data;
		this.countRec=countRec;
	}

	/*
	 * @We'll overried the getView method which is called for every ListItem we
	 * have.
	 * 
	 * @There are lots of different caching techniques for Android ListView to
	 * achieve better performace especially if you are going to have a very long
	 * ListView.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View listItem = convertView;

		// inflate the listview_item_row.xml parent
		LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
		listItem = inflater.inflate(layoutResourceId, parent, false);

		// get the elements in the layout
		
		ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
		TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);
		TextView numrec = (TextView) listItem.findViewById(R.id.numrec);
		 
		
		/*
		 * Set the data for the list item. You can also set tags here if you
		 * want.
		 */
		ObjectDrawerItem folder = data[position];
		
		if(position==1){
			numrec.setText(countRec[1]);
			numrec.setVisibility(View.VISIBLE);
        }else if(position==2){
        	numrec.setText(countRec[0]);
        	numrec.setVisibility(View.VISIBLE);
        }else{
        	numrec.setVisibility(View.INVISIBLE);
        }

		
		imageViewIcon.setImageResource(folder.icon);
		textViewName.setText(folder.name);
		
		return listItem;
	}

}