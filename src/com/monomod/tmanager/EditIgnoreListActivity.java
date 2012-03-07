package com.monomod.tmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class EditIgnoreListActivity extends Activity implements OnItemClickListener {
	
	ListView ignoreLV;
	TextView header;
	List<App> ignoreList = new ArrayList<App>();
	SharedPreferences ignoreArray;
	AppsArrayAdapter adapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ignorelist_view);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Edit ignore list");
		
		header = new TextView(this);
		header.setBackgroundColor(Color.LTGRAY);
		header.setTextColor(Color.BLACK);
		header.setText("Tap app to remove from ignore list.");
		ignoreLV = (ListView) findViewById(R.id.ignore_listview);
		ignoreLV.setOnItemClickListener(this);
		ignoreLV.addHeaderView(header, false, false);
		
		getIgnoreList();
		
		adapter = new AppsArrayAdapter(getApplicationContext(), R.layout.appsview_item, ignoreList);
		
		ignoreLV.setAdapter(adapter);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, TaskManagerActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void getIgnoreList() {
		ignoreArray = getSharedPreferences("ignore_array", 0);
        Map<String, ?> ignorePkgNames = ignoreArray.getAll();
        PackageManager pm = getPackageManager();
        ignoreList.clear();
        
        for(String pkgName : ignorePkgNames.keySet()) {
        	CharSequence title = null;
  		  	Drawable icon = null;
  		  	try {
  		  		icon = pm.getApplicationIcon(pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA));
  		  		title = pm.getApplicationLabel(pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA));
  		  	}catch(Exception e) {
  			  title = null;
  		  	}
        	if(title != null) {
        		App app = new App(title.toString(), pkgName, icon);
        		ignoreList.add(app);
        	}
        }
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int postion, long id) {
		SharedPreferences.Editor editor = ignoreArray.edit();
		editor.remove((String) view.getTag());
		editor.commit();
		getIgnoreList();
		adapter.notifyDataSetChanged();
		Toast.makeText(this, "App removed!", Toast.LENGTH_SHORT).show();
	}
	
}