package com.monomod.tmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppsArrayAdapter extends ArrayAdapter<AppsList> {

	private Context context;
	private List<AppsList> apps = new ArrayList<AppsList>();
	private TextView appName;
	private ImageView appIcon;
	private LinearLayout ll;
	
	public AppsArrayAdapter(Context context, int textViewResourceId,
			List<AppsList> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.apps = objects;
	}
	
	public int getCount() {
		return this.apps.size();
	}
	
	public AppsList getItem(int index) {
		return this.apps.get(index);
	}
	
	public View getView(int position, View appView, ViewGroup parent) {
		View row = appView;
		
		if(row == null) {
			Log.e("ArrayListAdapter", "Starting XML Row Inflation ... ");
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.appsview_item, parent, false);
			Log.e("ArrayListAdapter", "Successfully completed XML Row Inflation!");
		}
		
		AppsList app = getItem(position);
		
		appName = (TextView)row.findViewById(R.id.textView1);
		appIcon = (ImageView)row.findViewById(R.id.imageView1);
		ll = (LinearLayout)row.findViewById(R.id.ll);
		
		appName.setText(app.name);
		ll.setTag(app.pkgName);
		appIcon.setImageDrawable(app.icon);
		
		return row;
		
	}

}
