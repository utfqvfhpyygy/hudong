package com.aipaiadmin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class myPaint extends Activity
{
	
	private paint mPaint;
	
	private int uid;
	private int gid;
	private String tid;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mPaint = new paint(this);
		setContentView(mPaint);
		
		Intent myintIntent = getIntent();
		Log.d("pain", "¿ªÊ¼ pain === "+myintIntent.getStringExtra("uid"));
		uid =  Integer.parseInt( myintIntent.getStringExtra("uid") );
		gid =  Integer.parseInt( myintIntent.getStringExtra("gid") );
		tid =  myintIntent.getStringExtra("tid") ;		
		mPaint.setValue(uid, gid, tid);
		
		
		
		

		
		
	}
	
}