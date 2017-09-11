package com.database.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidhive.dashboard.R;

import com.database.model.Customer;


public class CustomerAdapter extends ArrayAdapter<Customer> implements Filterable {
	
			Context context;
			ViewHolder holder;
		    int layoutResourceId;   
		    String customername;
		    View row;
		    String act="";
		    ArrayList<Customer> data=new ArrayList<Customer>();
		    int [] photo = new int[] {R.drawable.corporate,R.drawable.residential,R.drawable.readcustomermeter,R.drawable.deletereading};
		    int icon;
		    ValueFilter valueFilter;
		    ArrayList<Customer> mStringFilterList;
		    private SparseBooleanArray mSelectedItemsIds;
		 
		    public CustomerAdapter(Context context, int layoutResourceId, ArrayList<Customer> data) {
		        super(context, layoutResourceId, data);
		        this.layoutResourceId = layoutResourceId;
		    	mSelectedItemsIds = new SparseBooleanArray();
		        this.context = context;
		        this.data = data;
		        mStringFilterList = data;
		    }
		   
		    public void setact(String action){
		    	this.act=action;
		    }
		    
		    public String getact(){
		    	return act;
		    }
		    @Override
		    public int getCount() {
		        return data.size();
		    }

		    @Override
		    public Customer getItem(int position) {
		        return data.get(position);
		    }

		    @Override
		    public long getItemId(int position) {
		        return data.indexOf(getItem(position));
		    }
		    @Override
		    public View getView(int position, View convertView, ViewGroup parent) {
		        row = convertView;
		        holder = null;
		       
		        if(row == null)
		        {
		            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		            row = inflater.inflate(layoutResourceId, parent, false);
		           
		            holder = new ViewHolder();
		            holder.cname = (TextView)row.findViewById(R.id.cname);
		            holder.connection_type=(TextView)row.findViewById(R.id.connection_type);
		            holder.meternum=(TextView)row.findViewById(R.id.meternum);
		            holder.result=(TextView)row.findViewById(R.id.result_found);
		            holder.stat=(TextView)row.findViewById(R.id.printStat);
		            holder.img=(ImageView)row.findViewById(R.id.icon);
		            holder.brgyAddress=(TextView)row.findViewById(R.id.BrgyAddress);
		            holder.standPipeAddress=(TextView)row.findViewById(R.id.standPipeAddress);
		            row.setTag(holder);
		        }
		        else
		        {
		            holder = (ViewHolder)row.getTag();
		        }
		       
		        Customer customer = data.get(position);
		        
		        holder.cname.setText(customer.getCorporate()+customer.getFname()+" "+customer.getMname()+" "+customer.getLname());
		        holder.connection_type.setText(customer.getConnection_type());
		        holder.meternum.setText(customer.getMeter_no());
		        holder.standPipeAddress.setText(" ["+customer.getPurok_no()+"]");
		        holder.brgyAddress.setText(customer.getBarangay());
		        
		        if(customer.getCorporate().isEmpty() && customer.getStat()!=2 && getact().isEmpty() ){
		        	icon=photo[1];
		        }else if(!customer.getCorporate().isEmpty() && customer.getStat()!=2 && getact().isEmpty()){
		        	icon=photo[0];
		        }else if(getact().isEmpty()){
		        	icon=photo[2];
		        }else{
		        	icon=photo[3];
		        }
		        holder.img.setImageResource(icon);
		        
		        if(customer.getStat()==1){
		        	holder.stat.setVisibility(View.VISIBLE);
		        }else{
		        	holder.stat.setVisibility(View.INVISIBLE);
		        }
		        
		        if(position % 2==0){
		        	row.setBackgroundResource(R.drawable.alternatecolor1);
		        }else{
		        	row.setBackgroundResource(R.drawable.alternatecolor2);
		        }
		        
		        
		        
		       return row;
		       
		    }
		   
		    static class ViewHolder
		    {
		        TextView cname;
		        TextView meternum;
		        TextView connection_type;
		        ImageView img;
		        TextView result;
		        TextView stat;
		        TextView standPipeAddress;
		        TextView brgyAddress;
		    }
		    
		    @Override
		    public Filter getFilter() {
		        if (valueFilter == null) {
		            valueFilter = new ValueFilter();
		        }
		        return valueFilter;
		    }
		    
		    private class ValueFilter extends Filter {
		        @Override
		        protected FilterResults performFiltering(CharSequence constraint) {
		            FilterResults results = new FilterResults();
		            
		            String constraintArray[]=((String) constraint).split("\\|");
		            String barangaySearch="";
		            String standPipe="";
		            String brgyOrStandPipe="";
		            boolean ambi_pipe=false;
		            
		            String custname[]=((String) constraint).split(" ");
		            String fname="";
		            String lname="";
		            String mname="";
		            String fnameOrLname="";
		       
		            //System.out.println(constraint);
		            if(!containPipe(constraint,"|")==true){
		            	
		            	if(custname.length==3){
			            	fname=custname[0];
			            	mname=custname[1];
			            	lname=custname[2];
			            }else if(custname.length==2){
			            	fname=custname[0];
			            	lname=custname[1];
			            }else{
			            	fnameOrLname=custname[0];
			            }
		            }else{
		            	if(constraintArray.length==3){
			            	barangaySearch=constraintArray[0];
			            	standPipe=constraintArray[1];
			            	if(standPipe.length()==3){
			            		ambi_pipe=true;
			            	}
			            	
			            
			            }else if(constraintArray.length==2){
			            	brgyOrStandPipe=constraintArray[0];
			            	
			            	if(brgyOrStandPipe.length()==3 && isPipe(brgyOrStandPipe,"-")){
			            		ambi_pipe=true;
			            	}
			            }
		            }
		            
		            System.out.println(barangaySearch+"|"+standPipe+" 1");
		            System.out.println(brgyOrStandPipe+" 2");
		            System.out.println(barangaySearch+" 3");
		            System.out.println(standPipe+" 4");
		            System.out.println(fnameOrLname+" 5");
		            System.out.println(fname+ " "+ lname+" "+ mname+" 6");
		            if(standPipe!=""){
		            	
		            	System.out.println(standPipe.substring(0, 3)+" 8");
		            	
		            }
		            
		        
		            if (constraint != null && constraint.length() > 0) {
		                ArrayList<Customer> filterList = new ArrayList<Customer>();
		                for (int i = 0; i < mStringFilterList.size(); i++) {
		                   
		                   if(fname!="" && lname!="" && mname !=""){
		                	   if((mStringFilterList.get(i).getFname().toUpperCase()).contains(fname.toString().toUpperCase()) 
		                		   && (mStringFilterList.get(i).getLname().toUpperCase()).contains(lname.toString().toUpperCase())
		                		   && (mStringFilterList.get(i).getMname().toUpperCase()).contains(mname.toString().toUpperCase())){
			                		Customer customer = recordSearch(i);
			                        filterList.add(customer);
			                	}
		                
		                   }else if(fname!="" && lname!=""){
		                	   if((mStringFilterList.get(i).getFname().toUpperCase()).contains(fname.toString().toUpperCase()) 
			                		   && (mStringFilterList.get(i).getLname().toUpperCase()).contains(lname.toString().toUpperCase())){
				                		Customer customer = recordSearch(i);
				                        filterList.add(customer);
				               }
		                   
		                   }else if(fnameOrLname !=""){
		                	   if((mStringFilterList.get(i).getCorporate().toUpperCase())
			                            .contains(constraint.toString().toUpperCase()) || (mStringFilterList.get(i).getFname().toUpperCase()).contains(fnameOrLname.toString().toUpperCase()) 
			                		   || (mStringFilterList.get(i).getLname().toUpperCase()).contains(fnameOrLname.toString().toUpperCase())
			                		   || (mStringFilterList.get(i).getMeter_no().toUpperCase().contains(constraint.toString().toUpperCase()))){
				                		Customer customer = recordSearch(i);
				                        filterList.add(customer);
				               }
		                   
		                   }else if(barangaySearch!="" && standPipe!="" ){
		                				                	
			                	if((mStringFilterList.get(i).getPurok_no().toUpperCase())
							            .contains(standPipe.toString().toUpperCase()) && (mStringFilterList.get(i).getBarangay().toUpperCase())
			                            .contains(barangaySearch.toString().toUpperCase()) && (mStringFilterList.get(i).getPurok_no().toUpperCase())
			                            .contains(standPipe.toString().toUpperCase())){
			                		Customer customer = recordSearch(i);
			                        filterList.add(customer);
			                	}
		                	}else if(brgyOrStandPipe!=""){
			                	
									if((mStringFilterList.get(i).getPurok_no().toUpperCase())
								            .contains(brgyOrStandPipe.toString().toUpperCase()) ||(mStringFilterList.get(i).getBarangay().toUpperCase())
								            .contains(brgyOrStandPipe.toString().toUpperCase()) || (mStringFilterList.get(i).getPurok_no().toUpperCase())
								            .contains(brgyOrStandPipe.toString().toUpperCase())){
										Customer customer = recordSearch(i);
								        filterList.add(customer);
									}
		                	}else if ((mStringFilterList.get(i).getFname().toUpperCase())
		                            .contains(constraint.toString().toUpperCase()) || (mStringFilterList.get(i).getCorporate().toUpperCase())
		                            .contains(constraint.toString().toUpperCase()) || (mStringFilterList.get(i).getMeter_no().toUpperCase())
		                            .contains(constraint.toString().toUpperCase())||(mStringFilterList.get(i).getLname().toUpperCase())
		                            .contains(constraint.toString().toUpperCase())||(mStringFilterList.get(i).getBarangay().toUpperCase())
		                            .contains(constraint.toString().toUpperCase())) {

		                        Customer customer = recordSearch(i);
		                        filterList.add(customer);
		                    }
		                }
		                results.count = filterList.size();
		                results.values = filterList;
		                
		            } else {
		                results.count = mStringFilterList.size();
		                results.values = mStringFilterList;
		            }
		            return results;

		        }


				private Customer recordSearch(int i) {
					Customer customer = new Customer(mStringFilterList.get(i)
					        .getCustomerid() ,  mStringFilterList.get(i)
					        .getFname(),  mStringFilterList.get(i)
					        .getMname(),  mStringFilterList.get(i)
					        .getLname(), mStringFilterList.get(i)
					        .getCorporate(), mStringFilterList.get(i)
					        .getConnection_type(), mStringFilterList.get(i)
					        .getMeter_no(), mStringFilterList.get(i)
					        .getBarangay(), mStringFilterList.get(i)
					        .getStat(),mStringFilterList.get(i)
					        .getPurok_no());
					return customer;
				}
		        
		     
		        @Override
		        protected void publishResults(CharSequence constraint,
		                FilterResults results) {
		            data = (ArrayList<Customer>) results.values;
		            notifyDataSetChanged();
		            
		           }
		     }
		    
		    public void toggleSelection(int position) {
		    	
				selectView(position, !mSelectedItemsIds.get(position));
			}

			public void selectView(int position, boolean value) {
				if (value){
					mSelectedItemsIds.put(position, value);
				}else{
					mSelectedItemsIds.delete(position);
				}
				
				notifyDataSetChanged();
			}
			
			public void removeSelection() {
				mSelectedItemsIds = new SparseBooleanArray();
				notifyDataSetChanged();
			}
			
			public SparseBooleanArray getSelectedIds() {
				return mSelectedItemsIds;
			}
			public void refreshOnPrintBill() {
				notifyDataSetChanged();
				
			}
			private boolean containPipe(CharSequence constraint, String charac){
				return ((String) constraint).contains(charac);
			}
			private boolean isPipe(String constraint, String charac){
				return ((String) constraint).contains(charac);
			}
}

