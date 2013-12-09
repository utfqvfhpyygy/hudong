package com.aipaiadmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.aipaiadmin.core.WifiAdmin;

public class MainActivity extends Activity implements OnClickListener{
	
	
    
    /** 打开按钮 **/
    private Button server1,server2,server3,server4;	
    
    
    
    private WifiAdmin wifiAdmin;
    private myHandler myHandler;
    private myThread myThread;
    private WifiManager wifiManager = null;  
    
    /** 指定热点SSID **/
    private  String wifiSSID = "";
    /** 指定热点名称 **/
    private  String wifiName = "";
    
    private Dialog dialog=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi);
        initUI();   

    }
    
    private void initUI(){
        server1 = (Button) findViewById(R.id.server1);
        server2 = (Button) findViewById(R.id.server2);
        server3 = (Button) findViewById(R.id.server3);
        server4 = (Button) findViewById(R.id.server4);

        server1.setOnClickListener(this);
        server2.setOnClickListener(this);
        server3.setOnClickListener(this);
        server4.setOnClickListener(this);
    }    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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
		
    		case 1:
    			MainActivity.this.finish();
    			System.exit(0);
    			break;
    		case 2:
//    			Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
//    			intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
//    			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    			startActivity(intent);    			
    	}
    	
    	return true;
//		return super.onOptionsItemSelected(item);
	} 
    @Override
    protected void onResume() {
        super.onResume();
//        initWifiConnection();
    }
   
    /*
     * 连接指定WIFI
     */
    private void chooseWifi()
    {
    	wifiAdmin = new WifiAdmin();
    	
    	wifiAdmin.initWifiConnection(this,wifiSSID,wifiName);

		myHandler = new myHandler();
		myThread  = new myThread(); 
		new Thread(myThread).start();

    }

    private void gotoIntent(Context context,Class classname)
    {
        Intent intent = new Intent();
        
        intent.setClass(context, classname);
        startActivity(intent);
        MainActivity.this.finish();     	
    }
    
    private void setValue(String ssid,String name)
    {
    	wifiSSID = ssid;
    	wifiName = name;
    }
    
    @Override
    public void onClick(View v) {
    	Log.d("wifi", "t"+v.getId());
    	switch (v.getId()) {
	        case R.id.server1:
	        	setValue("\"leyanwu2\"","leyanwu2");
	        	chooseWifi();
	            break;            
	        case R.id.server2:  
	        	setValue("\"leyanwu3\"","leyanwu3");
	        	chooseWifi();
	        	break;    
	        case R.id.server3:  	        	
	        	setValue("\"leyanwu5\"","leyanwu5");
	        	chooseWifi();
	        	break;
	        case R.id.server4:
//	            Intent intent = new Intent();
//	            
//	            intent.setClass(MainActivity.this, myPaint.class);
//	            startActivity(intent);
//	            MainActivity.this.finish();  	
	        	gotoIntent(MainActivity.this,com.aipaiadmin.mainbak.class);
	            break;
	        default:
	        	break;
        } 	
    }   
    
 
    public class BootBroadcastReceiver extends BroadcastReceiver
    {
    	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if ( intent.getAction().equals(ACTION)){
				Intent intent1 = new Intent(MainActivity.this,MainActivity.class);
				startActivity(intent1);
			}
		}
    	
    }
    
    /*
     * 新开一个线程去管理wifi是否设置好
     */
     class myHandler extends Handler
    {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.d("wifileyanwu", "接受wifi 是否更新成功"+msg.what);
			//0表示还没有更新完1表示已经更新完
			if ( msg.what == 0 ){
				
				if ( dialog == null ){
					dialog = new AlertDialog.Builder(MainActivity.this).setTitle("服务器配置").setMessage("进入服务器中,请等待...").create();
					dialog.show();
				}
				
			}else if ( msg.what == 2){
				Log.d("wifileyanwu", "开始重试连接wifi "+msg.what);
				chooseWifi();
			}else {
				if ( dialog != null)
				{
					dialog.dismiss();
				}
				MainActivity.this.gotoIntent(MainActivity.this,com.aipaiadmin.mainbak.class);
			}
				
			super.handleMessage(msg);
		}
    	
    }
    
    class myThread implements Runnable
    {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int k=0;
			while(true)
			{
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
		        wifiManager = (WifiManager) getSystemService(Service.WIFI_SERVICE);
		        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		        
		        Log.d("wifileyanwu", "检测wifi2情况"+wifiInfo.getSSID());
		        
		        String testNameString = "leyanwu1";
		        
		        if ( testNameString.equals(wifiInfo.getSSID()))
		        {
		        	myHandler.sendEmptyMessage(1);
		        	break;
		        }
		        else {
					
		        	if ( k == 8){
		        		myHandler.sendEmptyMessage(2);
		        	}else if ( k >= 15 ){
		        		break;
		        	}else{		        		
		        		myHandler.sendEmptyMessage(0);
		        	}
				}
		        k++;
//		        break;
			}
		}
    	
    }
}
