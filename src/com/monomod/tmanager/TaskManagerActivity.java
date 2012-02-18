package com.monomod.tmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskManagerActivity extends Activity implements OnItemClickListener {
    /** Called when the activity is first created. */
	


	ListView appsLV;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.main);
        

        
        ActivityManager actvityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listOfProcesses = actvityManager.getRunningAppProcesses();
        
        List<AppsList> appsList = new ArrayList<AppsList>();
        
		
		Iterator i = listOfProcesses.iterator();
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
		  
		  if(c != null && icon != null) {
			  AppsList app  = new AppsList(c.toString(),info.processName, icon);
			  appsList.add(app);
		  }
		  
		}
		
		Log.e("ListViewPart", "start");
		AppsArrayAdapter adapter = new AppsArrayAdapter(getApplicationContext(), R.layout.appsview_item, appsList);	
		Log.e("ListViewPart", "end");
		
		appsLV = (ListView)findViewById(R.id.appsLV);
		appsLV.setAdapter(adapter);
		appsLV.setOnItemClickListener(this);
		
        
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		String pkgName = ""+view.getTag();



        Toast.makeText(getApplicationContext(), pkgName, Toast.LENGTH_SHORT).show();
        
		//ActivityManager actvityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		
		//actvityManager.killBackgroundProcesses(pkgName);
	
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
}
