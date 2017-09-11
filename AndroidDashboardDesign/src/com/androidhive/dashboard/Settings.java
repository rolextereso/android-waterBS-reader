package com.androidhive.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidhive.dashboard.R;

import com.database.adapter.settingAdapter;
import com.navigation.drawer.activity.BaseActivity;

public class Settings extends BaseActivity implements OnItemClickListener {
	 /** Called when the activity is first created. */
	 ListView list;
	 String[] settingname={
			 "Account Setting",
			 "Formula Setting",
			 "Block/Edit User Account",
			 "Add New User",
			 "Printer's Name",
			 "Action Logs"
	 };
	 String[] settingdescrip={
			 "Let you edit your account's information",
			 "Set your water consumption formula",
			 "Able you to block/update users' account",
			 "Add new account to the system",
			 "Name of the printer usually the bluetooth connection name of the printer",
			 "All actions performed by the user are recorded"
	 };
	 
	 Integer[] imageId={
			 R.drawable.user_count,
			 R.drawable.formula,
			 R.drawable.block_user,
			 R.drawable.adduser,
			 R.drawable.printer_select,
			 R.drawable.logs
	 };
	// String usertype;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.places_layout, frameLayout);
		
		/**
		 * Setting title and itemChecked  
		 */
		mDrawerList.setItemChecked(6, true);
		setTitle(listArray[6]);
       
        ImageView icon=(ImageView)findViewById(R.id.systemicon);
        icon.setImageResource(R.drawable.setting_pressed);
        settingAdapter settingadapter = new
        		settingAdapter(this, settingname,settingdescrip, imageId);
		
        list=(ListView)findViewById(R.id.settinglist);
        list.setOnItemClickListener(this);
		list.setAdapter(settingadapter);
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		
		if(position==0){
			Intent intent = new Intent(getApplicationContext(),AccountSetting.class);
	        startActivity(intent);
		}else if(position==1){
			Intent intent = new Intent(getApplicationContext(),FormulaSetting.class);
	        startActivity(intent);
		}else if(position==2) {
			if(usertype.equals("Administrator")){
				Intent intent = new Intent(getApplicationContext(),BlockUser.class);
		        startActivity(intent);
			}else{
				Toast.makeText(getApplicationContext(), "You have no permission to open this module!", Toast.LENGTH_SHORT).show();
			}
			
		}else if(position==3) {
			if(usertype.equals("Administrator")){
				Intent intent = new Intent(getApplicationContext(),AddUser.class);
		        startActivity(intent);
			}else{
				Toast.makeText(getApplicationContext(), "You have no permission to open this module!", Toast.LENGTH_SHORT).show();
			}	
		}else if(position==4) {
			if (usertype.equals("Administrator")) {
				Intent intent = new Intent(getApplicationContext(),SelectPrinter.class);
		        startActivity(intent);
			}else{
				Toast.makeText(getApplicationContext(), "You have no permission to open this module!", Toast.LENGTH_SHORT).show();
			}
	    }else if(position==5) {
			if(usertype.equals("Administrator")){
				Intent intent = new Intent(getApplicationContext(),ActionLogs.class);
		        startActivity(intent);
			}else{
				Toast.makeText(getApplicationContext(), "You have no permission to open this module!", Toast.LENGTH_SHORT).show();
			}
	    }
}
}
