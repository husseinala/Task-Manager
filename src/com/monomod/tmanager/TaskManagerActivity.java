package com.monomod.tmanager;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class TaskManagerActivity extends Activity {
    /** Called when the activity is first created. */
	
	
	TextView tv;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView tv = (TextView) findViewById(R.id.tv1);
        
        ActivityManager actvityManager = (ActivityManager)
		this.getSystemService( ACTIVITY_SERVICE );
		List<RunningAppProcessInfo> procInfos = actvityManager.getRunningAppProcesses();
		for(int i = 0; i < procInfos.size(); i++)
		{
				tv.append(procInfos.get(i).processName + "\n");
				if(procInfos.get(i).processName.equals("com.android.camera")) {
					Toast.makeText(getApplicationContext(), "Camera App is running", Toast.LENGTH_LONG).show();
				}
		}
        
    }
}