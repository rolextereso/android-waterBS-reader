package com.androidhive.dashboard;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidhive.dashboard.R;

import com.database.adapter.CustomerAdapter;
import com.database.adapter.userAdapter;
import com.database.helper.DBHelper;
import com.database.model.Customer;
import com.database.model.Users;

public class BlockUser extends Activity implements OnItemClickListener {
	 /** Called when the activity is first created. */

	DBHelper mydb;
	ListView List;
	ArrayList<Users> userlist = new ArrayList<Users>();
	userAdapter adapter;
	List<Users> users;
	TextView nonumrow;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block_user_layout);
        ImageView icon=(ImageView)findViewById(R.id.systemicon);
        icon.setImageResource(R.drawable.block_user);
        TextView nonumrow=(TextView)findViewById(R.id.blockno_result_found);
        mydb=new DBHelper(this);
        
        users = mydb.All_User();
		for (Users u : users) {
			
			userlist.add(u);

		}
		adapter = new userAdapter(this, R.layout.block_user_row,userlist);
		
		   
	        if(adapter.isEmpty()){
	 			nonumrow.setVisibility(View.VISIBLE);
	 		}else{
	 			nonumrow.setVisibility(View.GONE);
	 		}
	         
		List = (ListView) findViewById(R.id.blockuserlist);
		List.setOnItemClickListener(this);
		List.setAdapter(adapter);
    }


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		//Toast.makeText(getApplicationContext(), users.get(position).getFname(), Toast.LENGTH_SHORT).show();
		
		int id_user = users.get(position).getId();
		String fname = users.get(position).getFname();
		String lname = users.get(position).getLname();
		String usertype = users.get(position).getUser_type();
		String userstat = users.get(position).getUser_stat();
		
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("id", id_user);
        dataBundle.putString("fname", fname);
        dataBundle.putString("lname", lname);
        dataBundle.putString("usertype", usertype);
        dataBundle.putString("userstat", userstat);
        Intent intent = new Intent(getApplicationContext(),BlockSpecificUser.class);
        intent.putExtras(dataBundle);
        startActivity(intent);
		
	} 


}