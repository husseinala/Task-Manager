/*
**
** Copyright 2012, Hussein Ala
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**       http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/


package com.monomod.tmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppsArrayAdapter extends ArrayAdapter<App> {

	private Context context;
	private List<App> apps = new ArrayList<App>();
	private TextView appName;
	private ImageView appIcon;
	private LinearLayout ll;
	
	public AppsArrayAdapter(Context context, int textViewResourceId,
			List<App> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.apps = objects;
	}
	
	public int getCount() {
		return this.apps.size();
	}
	
	public App getItem(int index) {
		return this.apps.get(index);
	}
	
	public View getView(int position, View appView, ViewGroup parent) {
		View row = appView;
		
		if(row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.appsview_item, parent, false);
		}
		
		App app = getItem(position);
		
		appName = (TextView)row.findViewById(R.id.textView1);
		appIcon = (ImageView)row.findViewById(R.id.imageView1);
		ll = (LinearLayout)row.findViewById(R.id.ll);
		
		appName.setText(app.name);
		ll.setTag(app.pkgName);
		appIcon.setImageDrawable(app.icon);
		
		return row;
		
	}

}
