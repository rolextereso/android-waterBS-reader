package com.database.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidhive.dashboard.R;

import com.database.model.Users;



public class userAdapter extends ArrayAdapter<Users> {
	
	Context context;
    int layoutResourceId;   
   
    ArrayList<Users> data=new ArrayList<Users>();
    public userAdapter(Context context, int layoutResourceId, ArrayList<Users> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ImageHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new ImageHolder();
            holder.username = (TextView)row.findViewById(R.id.blockUser);
            holder.usertype = (TextView)row.findViewById(R.id.blockusertype);
            holder.userstat = (TextView)row.findViewById(R.id.blockuserstat);
            row.setTag(holder);
        }
        else
        {
            holder = (ImageHolder)row.getTag();
        }
       
        Users user = data.get(position);
        holder.username.setText(user.getFname()+" "+user.getLname());
        holder.usertype.setText(user.getUser_type());
        if(user.getUser_stat().equals("Active")){
        	holder.userstat.setTextColor(Color.GREEN);
        }
        if(user.getUser_stat().equals("Unactive")){
        	holder.userstat.setTextColor(Color.RED);
        }
        holder.userstat.setText("["+user.getUser_stat()+"]");
        
       return row;
       
    }
   
    static class ImageHolder
    {
        
        TextView username;
        TextView usertype;
        TextView userstat;
    }
		    
		    
}

