package com.database.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidhive.dashboard.R;

public class importexportAdapter extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] settingname;
	private final String[] settingdescrip;
	private final Integer[] imageId;
	private final String[] values;

	public importexportAdapter(Activity context, String[] settingname, String[] settingdescrip, Integer[] imageId,String[] values) {
		super(context, R.layout.exportimport_row, settingname);
		this.context = context;
		this.settingname = settingname;
		this.settingdescrip = settingdescrip;
		this.imageId = imageId;
		this.values=values;

	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.exportimport_row, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.settingname);
		TextView txtDescript = (TextView) rowView.findViewById(R.id.settingdescription);
		TextView txtval = (TextView) rowView.findViewById(R.id.numortotal);
		
		ImageView imageView = (ImageView) rowView.findViewById(R.id.sicon);
		txtTitle.setText(settingname[position]);
		txtDescript.setText(settingdescrip[position]);
		txtval.setText(values[position]);

		imageView.setImageResource(imageId[position]);
		return rowView;
	}
}


