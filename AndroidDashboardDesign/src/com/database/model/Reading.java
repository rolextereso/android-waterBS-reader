package com.database.model;

public class Reading {
	  
	   int rid;
	   private String customerid;
	   private String pres_reading_date;
	   private String pres_reading_value;
	   private  double reading_amount;
	   private String reader;
	   private String datetime;
	   
	  	   
	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public Reading(){
		
	}
	
	public Reading(String customerid,String pres_reading_date, String pres_reading_value, double reading_amount,String reader,String datetime){
		this.customerid=customerid;
		this.pres_reading_date=pres_reading_date;
		this.pres_reading_value=pres_reading_value;
		this.reading_amount=reading_amount;
		this.reader=reader;
		this.datetime=datetime;
	}


	public String getReader() {
		return reader;
	}

	public void setReader(String reader) {
		this.reader = reader;
	}

	public int getRid() {
		return rid;
	}


	public String getCustomerid() {
		return customerid;
	}


	public String getPres_reading_date() {
		return pres_reading_date;
	}


	public String getPres_reading_value() {
		return pres_reading_value;
	}


	public double getReading_amount() {
		return reading_amount;
	}


	public void setRid(int rid) {
		this.rid = rid;
	}


	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}


	public void setPres_reading_date(String pres_reading_date) {
		this.pres_reading_date = pres_reading_date;
	}


	public void setPres_reading_value(String pres_reading_value) {
		this.pres_reading_value = pres_reading_value;
	}


	public void setReading_amount(double reading_amount) {
		this.reading_amount = reading_amount;
	}
	
	   
	   
	   
}
