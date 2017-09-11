package com.androidhive.dashboard;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import androidhive.dashboard.R;

import com.database.adapter.FileArrayAdapter;
import com.database.model.Item;

public class ActionLogs extends ListActivity {

	private File currentDir;
    private FileArrayAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        
        FileOperations file=new FileOperations();
        currentDir = new File(file.getPath());
        fill(currentDir); 
        
    }
    private void fill(File f)
    {
    	File[]dirs = f.listFiles(); 
		 this.setTitle("Action Logs: "+f.getName());
		 List<Item>dir = new ArrayList<Item>();
		 List<Item>fls = new ArrayList<Item>();
		 try{
			 for(File ff: dirs)
			 { 
				Date lastModDate = new Date(ff.lastModified()); 
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
			
				String icon="logs_icon";
				fls.add(new Item(ff.getName(),NumberFormat.getNumberInstance(Locale.US).format(ff.length()) + " Byte",  ff.getAbsolutePath(),icon));
				
			 }
		 }catch(Exception e)
		 {    
			 
		 }
		 Collections.sort(fls);
		 dir.addAll(fls);
		
		 adapter = new FileArrayAdapter(ActionLogs.this,R.layout.file_view,dir);
		 this.setListAdapter(adapter); 
    }
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Item o = adapter.getItem(position);
				
		Bundle dataBundle = new Bundle();
        dataBundle.putString("filePath", o.getPath());
        dataBundle.putString("fileName", o.getName());
       
        Intent intent = new Intent(getApplicationContext(),SpecificActionLog.class);
        intent.putExtras(dataBundle);
        startActivity(intent);
		
	}
   
   
}
