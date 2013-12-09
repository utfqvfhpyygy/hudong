package com.aipaiadmin.core;

import java.util.List;

import android.Manifest.permission;
import android.R.string;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WifiAdmin
{
	
    private final String TAG = getClass().getSimpleName();
    /** 指定热点SSID **/
    private  String wifiSSID = "\"leyanwu5\"";
    /** 指定热点名称 **/
    private  String wifiName = "leyanwu5";
    /** WIFI管理器 **/
    private WifiManager wifiManager = null;
    /** WIFI状态监听广播 **/
    private BroadcastReceiver receiver;
    /** 指定热点网络ID **/
    private int networkId;

    /** 指定热点是否已连接  **/
    private boolean targetWifiEnable = false;
    
	/**
     * Description 指定热点是否已配置(注册) 
     * @return true表示已注册，否则未注册
     */
    private boolean targetWifiIsConfig()
    {
        List<WifiConfiguration> wifiConfigurationList = wifiManager.getConfiguredNetworks();
        for (int i = 0; i < wifiConfigurationList.size(); i++) 
        {
            WifiConfiguration wifiConfiguration = wifiConfigurationList.get(i);
            
            Log.d("wifileyanwu", "已记住的WIFI"+wifiConfiguration.SSID);
            
            if (wifiConfiguration.SSID.equals(wifiSSID)) 
            {
            	Log.d("wifileyanwu", "获得WIFI的 networdid "+wifiConfiguration.networkId);
            	networkId = wifiConfiguration.networkId;
                return true;
            }
        }     
        return false;
    }	
    
    /**
     * Description 指定WIFI是否可以被扫描到，即是否在可用范围内
     * @return true表示可用，否则不可用
     */
    private boolean targetWifiCanScan(){
        int j = 0;
                
    	//while( j<=2 )
    	//{
	    	List<ScanResult> scanResultList = wifiManager.getScanResults();
	        if (scanResultList != null && scanResultList.size() > 0) {
	            for (int i = 0; i < scanResultList.size(); i++) 
	            {
	                ScanResult scanResult = scanResultList.get(i);
	                	
//		            Log.d("wifileyanwu", scanResult.SSID+" ====第 "+j+"次");
	           
	                if (scanResult.SSID.equals(wifiName)) 
	                {
	                	Log.d("wifileyanwu", "寻找到可用wifi "+scanResult.SSID);
	                	return true;
	                }
	            }
	        }
	    //    j++;
    	//}
    	

        return false;
    }  
    
    private boolean connectionTargetWifi(){
    	
    	Log.d("wifileyanwu", "开始wifi连接"+networkId);
    	if (wifiManager.enableNetwork(networkId, true)) 
    	{
            targetWifiEnable = true;
            Log.d("wifileyanwu", "wifi连接成功");                     
            return true;
        }else
        {
            Log.d("wifileyanwu", "wifi连接失败");
            return false;
        }
    } 
    
    public boolean initWifiConnection(Context context,String name,String wifiname)
    {
        wifiManager = (WifiManager) context.getSystemService(Service.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        
        Log.d("wifileyanwu", "检测wifi情况"+wifiInfo.getSSID()+"==========="+name);
        Log.d("wifileyanwu", "检测wifi情况"+wifiname+"==========="+name);
        wifiName = wifiname;
        wifiSSID = name;
        
        if ( wifiname.equals(wifiInfo.getSSID()) ) 
        {
            //return true;
        	//让应用每次都重新连接一次，避免之前的连接已经失效
        }
             
        if ( targetWifiIsConfig() ) //从已记住的WIFI里面匹配 
        {
            if ( targetWifiCanScan() ) 
            {

                return connectionTargetWifi();
            }

        }
        else  //从全部扫描到的WIFI里面寻找
        {
            if (!wifiManager.isWifiEnabled()) 
            {
                if (wifiManager.setWifiEnabled(true)) 
                {
                	Log.d("wifileyanwu", "尝试开启wifi");
                	
                	registerWifiChangeBoradCast(context);
                }else
                {
                    Log.d("wifileyanwu", "wifi开启失败");
                }
            }        	
        }
        
        return false;
    }  
    
    private void registerWifiChangeBoradCast(Context context){
        receiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                // WIFI可用时连接到指定热点
                System.out.println(wifiManager.getWifiState());
                
                if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    
                	Log.d("wifileyanwu", "wifi开启成功");
                   
                	unregisterWifiChangeReceiver(context);
                    handler.sendEmptyMessageDelayed(0, 10 * 1000);
                }
            }
        };
        
        IntentFilter filter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        String broadcastPermission = permission.ACCESS_WIFI_STATE;
        context.registerReceiver(receiver, filter, broadcastPermission, new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        });
    }
    
    /**
     * Description 注销WIFI状态改变监听广播 
     */
    private void unregisterWifiChangeReceiver(Context context){
        if (receiver != null) {
        	context.unregisterReceiver(receiver);
            
        }
    }    
    
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if (targetWifiCanScan()) {
                connectionTargetWifi();
            }
        }
    };     
}