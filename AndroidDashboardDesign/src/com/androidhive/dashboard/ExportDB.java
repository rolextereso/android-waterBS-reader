package com.androidhive.dashboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidhive.dashboard.R;
import au.com.bytecode.opencsv.CSVWriter;

import com.database.adapter.importexportAdapter;
import com.database.helper.DBHelper;
import com.navigation.drawer.activity.BaseActivity;

public class ExportDB extends BaseActivity {
	 /** Called when the activity is first created. */
	private static final int REQUEST_PATH = 1;
	private final String folderName="WBSExportedFiles/";
	
	String curFileName,curPath,countall,reading_amount="0";
	String[] values={"0","0"};
	EditText edittext,filename;
	DBHelper mydb;
	String path,pathfilename;
	
	int error;
	ListView list;
	 String[] name={
			 "No. of rows",
			 "Total consumption"
			 
	 };
	 String[] descrip={
			 "Total number of rows exported in the database",
			 "Total Water consumption exported in the database",
			 
	 };
	 
	 Integer[] imageId={
			 R.drawable.numrows,
			 R.drawable.water_consumption
			 
	 };
	 importexportAdapter importdb;
	//FileOperations file=new FileOperations();
	//String sfname,slname;
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.exportdb_layout, frameLayout);
		
		/**
		 * Setting title and itemChecked  
		 */
		mDrawerList.setItemChecked(5, true);
		setTitle(listArray[5]);
        //setContentView(R.layout.exportdb_layout); 
	    
        edittext = (EditText)findViewById(R.id.Exportdbtext);
        filename=(EditText)findViewById(R.id.filenamecsv);
        filename.setFocusable(false);
		filename.setClickable(false);
		
		mydb=new DBHelper(this);
		
		Date now = new Date();
        Date alsoNow = Calendar.getInstance().getTime();
        String nowAsString = new SimpleDateFormat("MMMMM dd, yyyy hh_mm_ss a").format(now);//yyyy-dd-MM
        
		filename.setText("WBS "+nowAsString+"("+sfname+").wbs");
		
		count_all_exported();
		
        
    }
    
    private void count_all_exported() {
		Cursor rs = mydb.count_all_export();
			rs.moveToFirst();
		     countall = rs.getString(rs
					.getColumnIndex("count_all"));
			   if(!countall.equals("0") ){
				     reading_amount = rs.getString(rs
								.getColumnIndex("reading_amount"));
				    
		        }
			   if (!rs.isClosed()) {
					rs.close();
				}
        
		 values[0]=NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(countall));
		 values[1]=NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(reading_amount));
		 importdb = new importexportAdapter(this, name,descrip, imageId,values);
		 list=(ListView)findViewById(R.id.exportdbs);
		 list.setAdapter(importdb);
		
	}
    
    
    public void getfile(View view){ 
    	Intent intent1 = new Intent(this, FileChooserExport.class);
        startActivityForResult(intent1,REQUEST_PATH);
    }
 // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
    	if (requestCode == REQUEST_PATH){
    		if (resultCode == RESULT_OK) { 
    			curFileName = data.getStringExtra("GetFileName"); 
    			curPath = data.getStringExtra("GetPath"); 
            	edittext.setText(curPath+"/");
            	mkeFolder(curPath+"/");
    		}
    	 }
    }
    
    public void exportDB(View v){

    	path=edittext.getText().toString();
    	pathfilename=filename.getText().toString();
    	if (""== path || path.trim().length() == 0 || path==null) {
    		Toast.makeText(getApplicationContext(), "Please select the path to export", Toast.LENGTH_SHORT).show();
		} else {
			ExportDatabaseCSVTask task=new ExportDatabaseCSVTask();
			task.execute();
		}
    }
    
    private class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean>{
	    private final ProgressDialog dialog = new ProgressDialog(ExportDB.this);
	    
	    @Override
	    protected void onPreExecute() {

	        this.dialog.setMessage("Exporting database...");
	        this.dialog.show();

	    }
	    protected Boolean doInBackground(final String... args){

	   
	        File file = new File(path+folderName+""+pathfilename);
	        try {

	            file.createNewFile();
	            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
	           
	            Cursor listExport=mydb.customer_leftjoin_readingval();
	            
	            String arrStr1[] ={"idcustomer", "r_val", "barangay", "c_date","reader"};
           	    csvWrite.writeNext(arrStr1);

		           while(listExport.moveToNext())  {
		        	   String arrStr[] ={listExport.getString(2),listExport.getString(23),
				                listExport.getString(16),listExport.getString(22),listExport.getString(25)};
		                /*String arrStr[] ={listExport.getString(21),listExport.getString(23),
		                listExport.getString(16),listExport.getString(22),listExport.getString(25)};*/
		                csvWrite.writeNext(arrStr);

		            }
	       	            csvWrite.close();
	            return true;
	        }
	        catch (IOException e){
	            System.out.println(e);
	            return false;
	        }
	    }
	    
	    @Override
	    protected void onPostExecute(final Boolean success)	{

	        if (this.dialog.isShowing()){
	            this.dialog.dismiss();
	        }
	        if (success){
	            Toast.makeText(ExportDB.this, "Export successfully!", Toast.LENGTH_SHORT).show();
				file.writeLogs(sfname+" "+slname+" export data from the system on "+file.dateTime()+" "+
	            			   ": Number of rows:"+values[0]+" Total consumption: "+values[1])	;
	           
	        }
	        else {
	            Toast.makeText(ExportDB.this, "Export failed!", Toast.LENGTH_SHORT).show();
	        }
	    }
	}
    private void mkeFolder(String path){
    	
    	File folder = new File(path+folderName);
     	
     	if (!folder.exists()) {
     	    folder.mkdir();
     	}
    	
     }
}
