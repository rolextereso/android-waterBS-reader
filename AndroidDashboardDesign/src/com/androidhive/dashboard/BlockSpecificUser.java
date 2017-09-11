package com.androidhive.dashboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidhive.dashboard.R;

import com.database.helper.DBHelper;
import com.database.model.Users;

public class BlockSpecificUser extends Activity {
	  
	TextView username,usertype,password;
	CheckBox block_user;
	int Value;
	String user_stat="Active",fname="",lname="",usertypes="",strPass="",userstat="";
	DBHelper mydb;
	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.block_user_specific);
	        
	        mydb=new DBHelper(this);
			username = (TextView) findViewById(R.id.specific_user);
			usertype = (TextView) findViewById(R.id.specificu_user_type);
			password=(TextView) findViewById(R.id.userpasswordedit);
			block_user=(CheckBox) findViewById(R.id.blockUsercheckbox);
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				Value = extras.getInt("id");
				fname= extras.getString("fname");
				lname= extras.getString("lname");
				usertypes= extras.getString("usertype");
				userstat= extras.getString("userstat");
				
				if(Value>0){
				   
				    username.setText(fname+" "+lname);
				    usertype.setText(usertypes);
				    }
			}
			if(userstat.equals("Active")){
				block_user.setChecked(false);
				user_stat="Active";
									
			}else{
				block_user.setChecked(true);
				user_stat="Unactive";				
			}
			 //if checkbox check it hide the block the user
		    block_user.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(block_user.isChecked()){
						user_stat="Unactive";
											
					}else{
						user_stat="Active";
										
					}
				}
			});
			
			
	        
	  }
	
	 	@SuppressWarnings("deprecation")
		public void saveblock(View view){
	 		strPass = password.getText().toString();
	 		AlertDialog alert = new AlertDialog.Builder(BlockSpecificUser.this)
			.create();
			alert.setTitle("Are you sure?");
			alert.setMessage("Are you sure want to edit "+ fname +" "+ lname+" account?");

			alert.setButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
			});
			alert.setButton2("Yes", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
						if(strPass.isEmpty() && block_user.isChecked()) {					
							Boolean updateStat=mydb.block_editstat(new Users(Value,user_stat));
							if(updateStat) {					
								Toast.makeText(getApplicationContext(), " "+fname+" "+lname+" account is successfully blocked! ", Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), "Error in updating the user", Toast.LENGTH_SHORT).show();
							}				
				 		}else if(!strPass.isEmpty() && !block_user.isChecked()){
				 			
				 			Boolean updatepass=mydb.block_editUser(new Users(Value,strPass,user_stat));
							if(updatepass) {					
								Toast.makeText(getApplicationContext(), " "+fname+" "+lname+" account password is successfully saved! ", Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), "Error in updating the user", Toast.LENGTH_SHORT).show();
							}
				 		}else if(!strPass.isEmpty() && block_user.isChecked()){
				 			
				 			Boolean updateRow=mydb.block_editUser(new Users(Value,strPass,user_stat));
							if(updateRow) {					
								Toast.makeText(getApplicationContext(), " "+fname+" "+lname+" account is block and password is successfully saved! ", Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), "Error in updating the user", Toast.LENGTH_SHORT).show();
							}
				 		}else if(strPass.isEmpty() && !block_user.isChecked()){
				 			
				 			Boolean updateRow=mydb.block_editstat(new Users(Value,strPass,user_stat));
							if(updateRow) {					
								Toast.makeText(getApplicationContext(), " "+fname+" "+lname+" account is unblocked! ", Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), "Error in updating the user", Toast.LENGTH_SHORT).show();
							}
				 		}else{
							
							Toast.makeText(getApplicationContext(), "Something Wrong", Toast.LENGTH_SHORT).show();
						}
						
						
					}
			});
			alert.show();
	 		
	 	}
}
