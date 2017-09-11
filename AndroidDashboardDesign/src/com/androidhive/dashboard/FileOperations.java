package com.androidhive.dashboard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;
 
public class FileOperations {
	
	private final String folderName="WBSlogs/";
	private  String path="/storage/sdcard1/";
	private String filename;
	private File pathFile;
	Date now = new Date();
    Date alsoNow = Calendar.getInstance().getTime();
	 
    public String getFilename() {
		return filename;
	}

	public void setFilename() {
		
		String nowAsString = filedateTime();
        this.filename="WBS Logs on "+nowAsString;

	}

	public String dateTime() {
		
        String nowAsString = new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss a").format(now);
		return nowAsString;
	}
	public String filedateTime() {
		
        String nowAsString = new SimpleDateFormat("MMMMM dd, yyyy").format(now);
		return nowAsString;
	}

	public String getPath() {
		return path+folderName;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public boolean getTotalDriveSize(){
		if(pathFile.getFreeSpace()<=512000){
			return true;
		}else{
			return false;
		}
		
	}
	public void displayDriveSize(){
		System.out.println(pathFile.getFreeSpace());
	}

	@SuppressLint("NewApi")
	public FileOperations() {
    	
    	    pathFile = new File(path);
      	
	      	if (pathFile.getTotalSpace()==0) {
	      		path="/storage/sdcard0/";
	      	}
	    	
	      	setFilename();
	     
    }
 
     public Boolean writeLogs(String fcontent){
            try {
            	
            	mkeFolder();
                String fpath = path+folderName+getFilename()+".txt";
 
                File file = new File(fpath);

                // If file does not exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(),true));
                bw.write("---------------------------------------");
                bw.newLine();
                bw.write("-> "+fcontent);
                bw.newLine();
                bw.close();
               
 
                Log.d("Suceess","Success");
                return true;
 
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
 
     }
 
     public String readLogs(String filepath){
 
         BufferedReader br = null;
         String response = null;
 
            try {
 
                StringBuffer output = new StringBuffer();
                String fpath = filepath;
 
                br = new BufferedReader(new FileReader(fpath));
                String line = "";
                while ((line = br.readLine()) != null) {
                    output.append(line +"\n");
                }
                response = output.toString();
 
            } catch (IOException e) {
                e.printStackTrace();
                return null;
 
            }
            return response;
 
     }
     private void mkeFolder(){
    	
    	File folder = new File(path+folderName);
     	
     	if (!folder.exists()) {
     	    folder.mkdir();
     	}
    	
     }
     
     
}