package com.database.model;

public class Users {
	  
	
	   int id;
	   String fname;
	   String lname;
	   String username;
	   String password;
	   String user_type;
	   String user_stat;
	   
	public Users(){
			
	}
	
	public Users(String fname, String lname, String username,String password,String user_type,String user_stat){
		this.fname=fname;
		this.lname=lname;
		this.username=username;
		this.password=password;
		this.user_type=user_type;
		this.user_stat=user_stat;
	}
	
	public Users(int id,String fname, String lname, String username,String password,String user_type,String user_stat){
		this.id=id;
		this.fname=fname;
		this.lname=lname;
		this.username=username;
		this.password=password;
		this.user_type=user_type;
		this.user_stat=user_stat;
	}
	
	public Users(int id,String password,String user_stat){
		this.id=id;
		if(!password.isEmpty()){
			this.password=password;
		}
		
		this.user_stat=user_stat;
	}
	
	public Users(int id,String user_stat){
		this.id=id;
		this.user_stat=user_stat;
	}


	public int getId() {
		return id;
	}


	public String getFname() {
		return fname;
	}


	public String getLname() {
		return lname;
	}


	public String getUsername() {
		return username;
	}


	public String getPassword() {
		return password;
	}


	public String getUser_type() {
		return user_type;
	}


	public String getUser_stat() {
		return user_stat;
	}


	public void setId(int id) {
		this.id = id;
	}


	public void setFname(String fname) {
		this.fname = fname;
	}


	public void setLname(String lname) {
		this.lname = lname;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}


	public void setUser_stat(String user_stat) {
		this.user_stat = user_stat;
	}
	
	   
	   
	   
	   
}
