package com.navigation.drawer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidhive.dashboard.R;

import com.androidhive.dashboard.AndroidDashboardDesignActivity;
import com.androidhive.dashboard.EmptyCustomerActivity;
import com.androidhive.dashboard.ExportDB;
import com.androidhive.dashboard.FileOperations;
import com.androidhive.dashboard.ImportDB;
import com.androidhive.dashboard.Login;
import com.androidhive.dashboard.ReadMeterActivity;
import com.androidhive.dashboard.Settings;
import com.androidhive.dashboard.UnreadMeterActivity;
import com.database.helper.DBHelper;

/**
 * @author dipenp
 * 
 *         This activity will add Navigation Drawer for our application and all
 *         the code related to navigation drawer. We are going to extend all our
 *         other activites from this BaseActivity so that every activity will
 *         have Navigation Drawer in it. This activity layout contain one frame
 *         layout in which we will add our child activity layout.
 */
public class BaseActivity extends Activity {

	/**
	 * Frame layout: Which is going to be used as parent layout for child
	 * activity layout. This layout is protected so that child activity can
	 * access this
	 * */
	protected FrameLayout frameLayout;

	/**
	 * ListView to add navigation drawer item in it. We have made it protected
	 * to access it in child class. We will just use it in child class to make
	 * item selected according to activity opened.
	 */

	protected ListView mDrawerList;

	/**
	 * List item array for navigation drawer items.
	 * */
	protected String[] listArray = { "WBS Reader-Dashboard", "Unread Meter",
			"Read Meter", "Empty Database", "Import Database",
			"Export Database", "Settings", "WBS Reader", "WBS Reader" };

	/**
	 * Static variable for selected item position. Which can be used in child
	 * activity to know which item is selected from the list.
	 * */
	protected static int position;

	/**
	 * This flag is used just to check that launcher activity is called first
	 * time so that we can open appropriate Activity on launch and make list
	 * item position selected accordingly.
	 * */
	private static boolean isLaunch = true;

	/**
	 * Base layout node of this Activity.
	 * */
	protected DrawerLayout mDrawerLayout;

	/**
	 * Drawer listner class for drawer open, close etc.
	 */
	private ActionBarDrawerToggle actionBarDrawerToggle;
	protected String sfname = "";
	protected String slname = "";
	protected String usertype = "";
	protected FileOperations file = new FileOperations();
    DBHelper mydb;
    
    protected String[] countRec={"0","0"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_drawer_base_layout);
		mydb=new DBHelper(this);
		
		if(file.getTotalDriveSize()){
			notifyMe("WBSlogs","WBS logs drive running out of space.","WBS Drive");
		}
		//file.displayDriveSize();

		SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0);
		sfname = settings.getString("fname", "").toString();
		slname = settings.getString("lname", "").toString();
		usertype = settings.getString("usertype", "").toString();

		frameLayout = (FrameLayout) findViewById(R.id.content_frame);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[9];

		drawerItem[0] = new ObjectDrawerItem(R.drawable.home_icon, "Home");
		drawerItem[1] = new ObjectDrawerItem(R.drawable.unread_meter_nav,
				"Unread Meter");
		drawerItem[2] = new ObjectDrawerItem(R.drawable.read_meter_icon,
				"Read Meter");
		drawerItem[3] = new ObjectDrawerItem(R.drawable.empty_db,
				"Empty Database");
		drawerItem[4] = new ObjectDrawerItem(R.drawable.import_icon,
				"Import Database");
		drawerItem[5] = new ObjectDrawerItem(R.drawable.export_icon,
				"Export Database");
		drawerItem[6] = new ObjectDrawerItem(R.drawable.setting_icon,
				"Settings");
		drawerItem[7] = new ObjectDrawerItem(R.drawable.log_out_icon, "Logout");
		drawerItem[8] = new ObjectDrawerItem(R.drawable.info_icon, "About");
		// drawerItem[3] = new ObjectDrawerItem(R.drawable, "Setting");

		countRec[0]=countUnprinted();
		countRec[1]=countUnread();
		
		// Pass the folderData to our ListView adapter
		DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this,
				R.layout.drawer_list_item, drawerItem,countRec);

		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				openActivity(position);
			}
		});

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		actionBarDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.open_drawer, /* "open drawer" description for accessibility */
		R.string.close_drawer) /* "close drawer" description for accessibility */
		{
			@Override
			public void onDrawerClosed(View drawerView) {
				getActionBar().setTitle(listArray[position]);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(getString(R.string.app_name));
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				super.onDrawerOpened(drawerView);
			}

		};
		mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

		/**
		 * As we are calling BaseActivity from manifest file and this base
		 * activity is intended just to add navigation drawer in our app. We
		 * have to open some activity with layout on launch. So we are checking
		 * if this BaseActivity is called first time then we are opening our
		 * first activity.
		 * */
		if (isLaunch) {
			/**
			 * Setting this flag false so that next time it will not open our
			 * first activity. We have to use this flag because we are using
			 * this BaseActivity as parent activity to our other activity. In
			 * this case this base activity will always be call when any child
			 * activity will launch.
			 */
			isLaunch = false;
			openActivity(0);
		}
	}

	

	/**
	 * @param position
	 * 
	 *            Launching activity when any list item is clicked.
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		actionBarDrawerToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	protected void openActivity(int position) {

		/**
		 * We can set title & itemChecked here but as this BaseActivity is
		 * parent for other activity, So whenever any activity is going to
		 * launch this BaseActivity is also going to be called and it will reset
		 * this value because of initialization in onCreate method. So that we
		 * are setting this in child activity.
		 */
		// mDrawerList.setItemChecked(position, true);
		// setTitle(listArray[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
		// Setting currently selected position
		// in this field so that it will be
		// available in our child
		// activities.
		switch (position) {
		case 0:
			BaseActivity.position = position;
			startActivity(new Intent(this, AndroidDashboardDesignActivity.class));
			break;
		case 1:
			BaseActivity.position = position;
			startActivity(new Intent(this, UnreadMeterActivity.class));
			break;
		case 2:
			BaseActivity.position = position;
			startActivity(new Intent(this, ReadMeterActivity.class));
			break;
		case 3:
			if (usertype.equals("Administrator")) {
				BaseActivity.position = position;
				startActivity(new Intent(this, EmptyCustomerActivity.class));
			} else {
				Toast.makeText(getApplicationContext(),
						"You have no permission to open this module!",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 4:
			if (usertype.equals("Administrator")) {
				BaseActivity.position = position;
				startActivity(new Intent(this, ImportDB.class));
			} else {
				Toast.makeText(getApplicationContext(),
						"You have no permission to open this module!",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case 5:
			if (usertype.equals("Administrator")) {
				BaseActivity.position = position;
				startActivity(new Intent(this, ExportDB.class));
			} else {
				Toast.makeText(getApplicationContext(),
						"You have no permission to open this module!",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 6:
			BaseActivity.position = position;
			startActivity(new Intent(this, Settings.class));
			break;
		case 7:
			logout_app();
			break;
		case 8:
			about_app();
			break;

		default:
			break;
		}
		// BaseActivity.position = position;

	}

	/* We can override onBackPressed method to toggle navigation drawer */
	public void logout_app() {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle("Are you sure?");
		alert.setIcon(R.drawable.h);

		alert.setMessage("Are you sure you want to close the application?");

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

				file.writeLogs(sfname + " " + slname
						+ " log-out the system on " + file.dateTime());

				SharedPreferences settings = getSharedPreferences(
						Login.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.remove("logged");
				editor.remove("fname");
				editor.remove("lname");
				editor.remove("usertype");
				editor.remove("id");
				editor.commit();
				finish();
				Intent intent = new Intent(getApplicationContext(), Login.class);
				startActivity(intent);
				

			}
		});
		alert.show();
	}

	public void about_app() {
		// Create custom dialog object
		final Dialog dialog = new Dialog(this);
		// Include dialog.xml file

		dialog.setContentView(R.layout.dialogcustom);
		dialog.setTitle("About");

		TextView text = (TextView) dialog.findViewById(R.id.dialogtext);
		// text.setText("Custom dialog Android example.");
		dialog.show();
	}

	@Override
	public void onBackPressed() {

		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			mDrawerLayout.openDrawer(mDrawerList);
		}

	}
	  public void notifyMe(String title, String body,String subject) {
			 
			NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	        Notification notify=new Notification(R.drawable.iconlauncher,title,System.currentTimeMillis());
	        PendingIntent pending= PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
	        
	        notify.setLatestEventInfo(getApplicationContext(),subject,body,pending);
	        notif.notify(0, notify);
		}
	  public String countUnprinted(){
		  Cursor rs = mydb.unprintedBillCustomer();
			rs.moveToFirst();
		     String countall = rs.getString(rs
					.getColumnIndex("notPrinted"));
			   
			   if (!rs.isClosed()) {
					rs.close();
				}
		   return countall;
	  }
	  private String countUnread() {
		  Cursor rs = mydb.countUnreadCustomer();
			rs.moveToFirst();
		     String countall = rs.getString(rs
					.getColumnIndex("unRead"));
			   
			   if (!rs.isClosed()) {
					rs.close();
				}
		   return countall;
			
	}
}
