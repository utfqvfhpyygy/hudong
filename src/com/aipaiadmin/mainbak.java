package com.aipaiadmin;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class mainbak extends Activity {
	
	WebView webView;
	String t1;
	private PowerManager.WakeLock mWakeLock;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
             
        ConnectivityManager manage = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo network = manage.getActiveNetworkInfo();
        
        Intent gaoIntent = getIntent();
        String uidString = gaoIntent.getStringExtra("uids");
       
        
       if ( network == null || !network.isAvailable())
       {
        	Toast.makeText(this, "当前没有网络可以用！", Toast.LENGTH_LONG).show();
        	
	        Intent intent = new Intent();
	        
	        intent.setClass(mainbak.this, leyanwu.class);
	        startActivity(intent);
	        mainbak.this.finish();   	        	
       }

	   
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "XYTEST");
		mWakeLock.acquire();
		batteryLevel();
        webView = new WebView(this);
        //this.webView=(WebView) this.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.requestFocusFromTouch();

        webView.setScrollBarStyle(webView.SCROLLBARS_INSIDE_OVERLAY); 
        Log.d("pain", "调用pain后返回的uid=="+uidString);
        if ( uidString != null ){
        	webView.loadUrl("http://192.168.1.140/tpad.php?action=get&uid="+uidString);
        }else {
        	webView.loadUrl("http://192.168.1.140/tpad.php");
        }
//        Toast.makeText(this, "http://192.168.1.140/tpad.php?b="+t1, Toast.LENGTH_LONG).show();
        webView.setWebViewClient(new WebViewClientDemo());  
        
//        webView.loadUrl("javascript:setBattery('20')");
        
        webView.setDownloadListener(new DownloadListener(){

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				// TODO Auto-generated method stub
		        Uri uri = Uri.parse(url);  
	            Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
	            startActivity(intent); 				
			}      	       	
        });
        
        webView.addJavascriptInterface(new Object(){
        	
        	public void startPain(String uid,String gid,String tid)
        	{
                Intent intent = new Intent();
                
                Log.d("pain", "调用js函数=="+uid);
                intent.setClass(mainbak.this, myPaint.class);
                intent.putExtra("uid", uid);
                intent.putExtra("gid", gid);
                intent.putExtra("tid", tid);
                startActivity(intent);
//                mainbak.this.finish();         		
        	}
        	
        }, "leyanwu");
    }
    
    private void batteryLevel()
    {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() { 
            public void onReceive(Context context, Intent intent) { 
                context.unregisterReceiver(this); 
                int rawlevel = intent.getIntExtra("level", -1);  //获得当前电量 
                int scale = intent.getIntExtra("scale", -1); 
                //获得总电量 
                int level = -1; 
                if (rawlevel >= 0 && scale > 0) { 
                    level = (rawlevel * 100) / scale; 
                } 
                
//              t1.setText("Battery Level Remaining: " + level + "%"); 
                t1 = String.valueOf(level);      		
                //统计电量信息
//        		HttpGet httpget = new HttpGet("http://192.168.1.140/tpad.php?b=="+t1+"&action=battery");
        		Log.d("bettery","ccc"+t1);
                webView.loadUrl("javascript:setBattery('"+t1+"')");
                
        		//try
        		//{
//        			HttpClient httpclient = new DefaultHttpClient();
//        			httpclient.execute(httpget);
        		//} 	
        		//catch (Exception e) {
        			// TODO: handle exception
        		//}	                
            } 
        }; 
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED); 
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);     
//        Log.d("bettery","cccc");
    }    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        super.onCreateOptionsMenu(menu);
    	menu.add(0,0,0,R.string.menu_settings);
    	menu.add(0,1,1,R.string.menu_loginout);
        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
    	int item_id = item.getItemId();
    	
    	switch ( item_id )
    	{
    		case 0:	        
    			//webView.loadUrl("javascript:setBattery('20')");
    			break;
    		case 1:
    	       
    			if (mWakeLock != null && mWakeLock.isHeld()) {  
    				mWakeLock.release();  
    				mWakeLock = null;  
    	        }      			
    			
    			mainbak.this.finish();
    			System.exit(0);
    			break;
    	}
    	
    	return true;
//		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		//return super.onKeyDown(keyCode, event);
    	
    	if ( keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack())
    	{
    		webView.goBack();
    		return true;
    	}
    	
//    	return true;
    	
    	return super.onKeyDown(keyCode, event);
	}




	private class WebViewClientDemo extends WebViewClient    {
        @Override
        // 在WebView中而不是默认浏览器中显示页面
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			// TODO Auto-generated method stub
			//super.onReceivedHttpAuthRequest(view, handler, host, realm);
			//handler.proceed("admin", "aipaiuser@2011");
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			setContentView(view);
			batteryLevel();
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
//        	Toast.makeText(this, "当前没有网络可以用！", Toast.LENGTH_LONG).show();
        	
	        Intent intent = new Intent();
	        
	        intent.setClass(mainbak.this, leyanwu.class);
	        startActivity(intent);
	        mainbak.this.finish();   			
			
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
		
		
        
        
    }    
}
