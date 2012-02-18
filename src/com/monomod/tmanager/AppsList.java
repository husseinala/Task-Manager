package com.monomod.tmanager;

import android.graphics.drawable.Drawable;

public class AppsList {
	public String name;
	public String pkgName;
	public Drawable icon;
	
	public AppsList() {
		
	}
	
	public AppsList(String name, String pkgName, Drawable icon) {
		this.name = name;
		this.pkgName = pkgName;
		this.icon = icon;
	}
	
	@Override
	public String toString() {
		return this.name;
	}



}
