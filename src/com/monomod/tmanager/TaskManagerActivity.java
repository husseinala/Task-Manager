package com.monomod.tmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class TaskManagerActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnClickListener {
    /** Called when the activity is first created. */
	


	ListView appsLV;
	Button endAll;
	List<AppsList> appsList = new ArrayList<AppsList>();
	AppsArrayAdapter adapter;	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.main);
        

        
        getAppsList();
		
		adapter = new AppsArrayAdapter(getApplicationContext(), R.layout.appsview_item, appsList);	
		
		appsLV = (ListView)findViewById(R.id.appsLV);
		appsLV.setAdapter(adapter);
		appsLV.setOnItemClickListener(this);
		appsLV.setOnItemLongClickListener(this);
		endAll = (Button)findViewById(R.id.killAll);
		endAll.setOnClickListener(this);
		
        
    }
    
    public void getAppsList() {
    	
    	ActivityManager actvityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listOfProcesses = actvityManager.getRunningAppProcesses();
        
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
			  Log.e("NameNotFound: ", info.processName);
		  }
		  
		  if(c != null && icon != null && !(contains(info.processName, "com.android.systemui"))) {
			  AppsList app  = new AppsList(c.toString(),info.processName, icon);
			  appsList.add(app);
		  }
		  
		}
		

	
    	
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		String pkgName = ""+view.getTag();
		
		if(pkgName.equals("com.monomod.tmanager")) {
			this.finish();
		}

		killApp(pkgName);
 		getAppsList();
 		adapter.notifyDataSetChanged();
 		Toast.makeText(getApplicationContext(), "App killed!", Toast.LENGTH_SHORT).show();
	
	}
	

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
	    intent.setData(Uri.parse("package:"+view.getTag()));
	    startActivity(intent);
		
		return false;
	}
	
	public void killApp(String pkgName) {
		ActivityManager actvityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
 		actvityManager.restartPackage(pkgName);	

	}
	
	public boolean contains(String str1, String str2) {
	
		
		for(int i = 0; i<= (str1.length() - str2.length()); i++) {
			String subString = str1.substring(i, (i+str2.length()));
			if(subString.equalsIgnoreCase(str2)) {
				return true;
			}
		}
		
		return false;
	}
	


	@Override
	public void onClick(View arg0) {
		new EndAllTask().execute(appsList);
		
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
	     }
	 }

}
