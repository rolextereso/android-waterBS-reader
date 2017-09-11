package com.androidhive.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidhive.dashboard.R;

public class SpecificActionLog extends Activity{
	 /** Called when the activity is first created. */
	
	TextView fcontent,filenameView;
	String filepath="";
	String filename="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	        setContentView(R.layout.actionlogs_layout);
	        
	        FileOperations file=new FileOperations();
	        
	        ImageView icon=(ImageView)findViewById(R.id.systemicon);
	        icon.setImageResource(R.drawable.logs_icon);
	        
	        Bundle extras = getIntent().getExtras();
			if (extras != null) {
				
				filepath= extras.getString("filePath");
				filename= extras.getString("fileName");
	
			}
	        fcontent = (TextView)findViewById(R.id.logContent);
	        filenameView = (TextView)findViewById(R.id.filename);
	        
	        filenameView.setText(filename);
	        
	        String text = file.readLogs(filepath);
	        if(text != null){
	        	fcontent.setText(text);
	        }else {
                Toast.makeText(getApplicationContext(), "File not Found", Toast.LENGTH_SHORT).show();
                fcontent.setText(null);
            }
	        
	       	
		
    }

	

}
