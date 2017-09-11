package com.database.model;

public class Customer {
	  
	   private int id;
	   private String customerid;
	   private String customer_info_id;
	   private String fname;
	   private String mname;
	   private String lname;
	   private  String corporate;
	   private String barangay;
	   private String connection_type;
	   private String meter_no;
	   private String meter_id;
	   private double prev_amount;
	   private String pres_reading_date;
	   private double pres_reading_value;
	   private double cur_meter_bal;
	   private double penalty_amount;
	   private double reconnection;
	   private int barangay_id;
	   private int stat;
	   private String purok_no;
	   
	   
	public Customer(){
		
	}
	public Customer(String customerid,String customer_info_id,String fname, String mname, String lname, String corporate,
							String barangay, String connection_type,String meter_no,String meter_id,
							double prev_amount, String pres_reading_date, double pres_reading_value, double cur_meter_bal,
							double penalty_amount,double reconnection, int barangay_id, int stat, String purok_no){
		this.customerid=customerid;
		this.customer_info_id=customer_info_id;
		this.fname=fname;
		this.mname=mname;
		this.lname=lname;
		this.corporate=corporate;
		this.barangay=barangay;
		this.connection_type=connection_type;
		this.meter_no=meter_no;
		this.meter_id=meter_id;
		this.prev_amount=prev_amount;
		this.pres_reading_date=pres_reading_date;
		this.pres_reading_value=pres_reading_value;
		this.cur_meter_bal=cur_meter_bal;
		this.penalty_amount=penalty_amount;
		this.reconnection=reconnection;
		this.barangay_id=barangay_id;
		this.stat=stat;
		this.purok_no=purok_no;
		
	}
	public String getPurok_no() {
		return purok_no;
	}
	public void setPurok_no(String purok_no) {
		this.purok_no = purok_no;
	}
	public String getCustomer_info_id() {
		return customer_info_id;
	}
	public void setCustomer_info_id(String customer_info_id) {
		this.customer_info_id = customer_info_id;
	}
	public Customer(String customerid,String fname,String mname, String lname, String corporate,
			 String connection_type,String meter_no,String barangay,int stat, String purok_no){
			this.customerid=customerid;
			this.fname=fname;
			this.mname=mname;
			this.lname=lname;
			this.corporate=corporate;
			this.connection_type=connection_type;
			this.meter_no=meter_no;
			this.barangay=barangay;
			this.stat=stat;
			this.purok_no=purok_no;
			
    }
	public int getId() {
		return id;
	}
	public String getCustomerid() {
		return customerid;
	}
	public String getFname() {
		return fname;
	}
	public String getLname() {
		return lname;
	}
	public String getCorporate() {
		return corporate;
	}
	public String getConnection_type() {
		return connection_type;
	}
	public String getMeter_no() {
		return meter_no;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	public void setCorporate(String corporate) {
		this.corporate = corporate;
	}
	public void setConnection_type(String connection_type) {
		this.connection_type = connection_type;
	}
	public void setMeter_no(String meter_no) {
		this.meter_no = meter_no;
	}
	public String getMname() {
		return mname;
	}
	public String getBarangay() {
		return barangay;
	}
	public String getMeter_id() {
		return meter_id;
	}
	public double getPrev_amount() {
		return prev_amount;
	}
	public String getPres_reading_date() {
		return pres_reading_date;
	}
	public double getPres_reading_value() {
		return pres_reading_value;
	}
	public double getCur_meter_bal() {
		return cur_meter_bal;
	}
	public double getPenalty_amount() {
		return penalty_amount;
	}
	public double getReconnection() {
		return reconnection;
	}
	public void setMname(String mname) {
		this.mname = mname;
	}
	public void setBarangay(String barangay) {
		this.barangay = barangay;
	}
	public void setMeter_id(String meter_id) {
		this.meter_id = meter_id;
	}
	public void setPrev_amount(double prev_amount) {
		this.prev_amount = prev_amount;
	}
	public void setPres_reading_date(String pres_reading_date) {
		this.pres_reading_date = pres_reading_date;
	}
	public void setPres_reading_value(double pres_reading_value) {
		this.pres_reading_value = pres_reading_value;
	}
	public void setCur_meter_bal(double cur_meter_bal) {
		this.cur_meter_bal = cur_meter_bal;
	}
	public void setPenalty_amount(double penalty_amount) {
		this.penalty_amount = penalty_amount;
	}
	public void setReconnection(double reconnection) {
		this.reconnection = reconnection;
	}
	public int getBarangay_id() {
		return barangay_id;
	}
	public void setBarangay_id(int barangay_id) {
		this.barangay_id = barangay_id;
	}
	public int getStat() {
		return stat;
	}
	public void setStat(int stat) {
		this.stat = stat;
	}
	   
	   
	   
	   
}
