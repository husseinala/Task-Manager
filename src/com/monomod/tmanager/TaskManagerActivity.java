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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;	
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TaskManagerActivity extends Activity implements OnItemClickListener, OnClickListener {
    /** Called when the activity is first created. */
	


	ListView appsLV;
	Button endAll;
	Button exitBt;
	TextView noApps;
	TextView memInfoTv;
	ProgressBar avalMemPB;
	List<AppsList> appsList = new ArrayList<AppsList>();
	List<String> ignoreList = new ArrayList<String>();
	AppsArrayAdapter adapter;
	SharedPreferences ignoreArray;
	double totalMemory;
	double availableMemory;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        appsLV = (ListView)findViewById(R.id.apps_list_view);
		endAll = (Button)findViewById(R.id.kill_all_bt);
		exitBt = (Button)findViewById(R.id.exit_bt);
		noApps = (TextView) findViewById(R.id.no_bg_app_bt);
		avalMemPB = (ProgressBar) findViewById(R.id.aval_mem_pb);
		memInfoTv = (TextView) findViewById(R.id.mem_info_tv);
		totalMemory = 0;
		availableMemory = 0;
        
		appsLV.setOnItemClickListener(this);
		registerForContextMenu(appsLV);
		endAll.setOnClickListener(this);
		exitBt.setOnClickListener(this);
		
        ignoreArray = getSharedPreferences("ignore_aray", 0);
        Map<String, ?> test = ignoreArray.getAll();
        ignoreList.addAll(test.keySet());
        
        getAppsList();
        checkNoAppsRunning();
        getMemInfo();
        
		adapter = new AppsArrayAdapter(getApplicationContext(), R.layout.appsview_item, appsList);	
		
		appsLV.setAdapter(adapter);
		
		
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.options_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch(item.getItemId()) {
    	case R.id.exit:
    		this.finish();
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        AppsList app =  (AppsList) appsLV.getItemAtPosition(info.position);
        menu.setHeaderIcon(app.icon);
        menu.setHeaderTitle(app.name);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }
    
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        AppsList app =  (AppsList) appsLV.getItemAtPosition(info.position);
        String pkgName = app.pkgName;
        
        switch (item.getItemId()) {
            case R.id.cmenu_end_app:
            	killApp(pkgName);
         		getAppsList();
         		adapter.notifyDataSetChanged();
         		Toast.makeText(getApplicationContext(), "App killed!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.cmenu_ignore_app:
            	ignoreApp(pkgName);
         		return true;
            case R.id.cmenu_app_info:
            	getAppInfo(pkgName);
            	return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    
    public void getAppsList() {
    	
    	ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
    	
        List<ActivityManager.RunningAppProcessInfo> listOfProcesses = activityManager.getRunningAppProcesses();
        
        appsList.clear();
        
		Iterator<RunningAppProcessInfo> i = listOfProcesses.iterator();
		PackageManager pm = getPackageManager();
		while(i.hasNext()) {
		  ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
		  CharSequence c = null;
		  Drawable icon = null;
		  try {
			icon = pm.getApplicationIcon(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
		    c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
		  }catch(Exception e) {
			  c = null;
		  }
		  
		  if(c != null && icon != null && !(checkIfIgnore(info.processName))) {
			  AppsList app  = new AppsList(c.toString(),info.processName, icon);
			  appsList.add(app);
		  }
		  
		}	
		
    }
    
    public void checkNoAppsRunning() {
    	if(appsList.size() > 0) {
			appsLV.setVisibility(View.VISIBLE);
			endAll.setVisibility(View.VISIBLE);
			exitBt.setVisibility(View.GONE);
			noApps.setVisibility(View.GONE);
		} else {
			noApps.setVisibility(View.VISIBLE);
			exitBt.setVisibility(View.VISIBLE);
			appsLV.setVisibility(View.GONE);
			endAll.setVisibility(View.GONE);
		}
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		String pkgName = (String)view.getTag();
		
		if(pkgName.equals("com.monomod.tmanager")) {
			this.finish();
		}

		killApp(pkgName);
 		getAppsList();
 		adapter.notifyDataSetChanged();
 		checkNoAppsRunning();
 		getMemInfo();
 		Toast.makeText(getApplicationContext(), "App killed!", Toast.LENGTH_SHORT).show();
	
	}
	


	public void getAppInfo(String pkgName) {
		Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
	    intent.setData(Uri.parse("package:"+pkgName));
	    startActivity(intent);
	}
	
	public void ignoreApp(String pkgName) {
		SharedPreferences.Editor editor = ignoreArray.edit();
        editor.putString(pkgName, pkgName);
        editor.commit();
        ignoreList.add(pkgName);
    	getAppsList();
 		adapter.notifyDataSetChanged();
 		Toast.makeText(getApplicationContext(), "App added to ignore list!", Toast.LENGTH_SHORT).show();
	}
	
	public boolean checkIfIgnore(String pkgName) {
		return ignoreList.contains(pkgName);
		
	}
	
	public void killApp(String pkgName) {
		ActivityManager actvityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
 		actvityManager.restartPackage(pkgName);	

	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.kill_all_bt:
			new EndAllTask().execute(appsList);
			break;
		case R.id.exit_bt:
			this.finish();
			break;
		}
		
		
	}
	
	 private class EndAllTask extends AsyncTask<List<AppsList>, Integer, Long> {
	     protected Long doInBackground(List<AppsList>... names) {
	    	 
	    	 Iterator<AppsList> i = names[0].iterator();
	    	 while(i.hasNext()) {
	    		 killApp(i.next().pkgName);
	    	 }

	 		getAppsList();
			return null;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	     }

	     protected void onPostExecute(Long result) {
	    	 Toast.makeText(getApplicationContext(), "All apps killed!", Toast.LENGTH_SHORT).show();
	    	 adapter.notifyDataSetChanged();
	    	 checkNoAppsRunning();
	    	 getMemInfo();
	     }
	 }
	 
	 
	 private void getMemInfo() {
		 if(totalMemory == 0) {
		    try {
		        RandomAccessFile reader = new RandomAccessFile("/proc/meminfo", "r");
		        String load = reader.readLine();
		        String[] memInfo = load.split(" ");
		        totalMemory = Double.parseDouble(memInfo[9])/1024;

		    } catch (IOException ex) {
		        ex.printStackTrace();
		    }
		 }
		    
		    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		    MemoryInfo mi = new MemoryInfo();
		    activityManager.getMemoryInfo(mi);
		    availableMemory = mi.availMem / 1048576L;
		    memInfoTv.setText("Ram Info: Available: "+(int)(availableMemory)+"MB Total: "+(int)totalMemory+"MB.");
		    int progress = (int) (((totalMemory-availableMemory)/totalMemory)*100);
		    avalMemPB.setProgress(progress);
		    
		    
		} 

}
