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
    /** ָ���ȵ�SSID **/
    private  String wifiSSID = "\"leyanwu5\"";
    /** ָ���ȵ����� **/
    private  String wifiName = "leyanwu5";
    /** WIFI������ **/
    private WifiManager wifiManager = null;
    /** WIFI״̬�����㲥 **/
    private BroadcastReceiver receiver;
    /** ָ���ȵ�����ID **/
    private int networkId;

    /** ָ���ȵ��Ƿ�������  **/
    private boolean targetWifiEnable = false;
    
	/**
     * Description ָ���ȵ��Ƿ�������(ע��) 
     * @return true��ʾ��ע�ᣬ����δע��
     */
    private boolean targetWifiIsConfig()
    {
        List<WifiConfiguration> wifiConfigurationList = wifiManager.getConfiguredNetworks();
        for (int i = 0; i < wifiConfigurationList.size(); i++) 
        {
            WifiConfiguration wifiConfiguration = wifiConfigurationList.get(i);
            
            Log.d("wifileyanwu", "�Ѽ�ס��WIFI"+wifiConfiguration.SSID);
            
            if (wifiConfiguration.SSID.equals(wifiSSID)) 
            {
            	Log.d("wifileyanwu", "���WIFI�� networdid "+wifiConfiguration.networkId);
            	networkId = wifiConfiguration.networkId;
                return true;
            }
        }     
        return false;
    }	
    
    /**
     * Description ָ��WIFI�Ƿ���Ա�ɨ�赽�����Ƿ��ڿ��÷�Χ��
     * @return true��ʾ���ã����򲻿���
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
	                	
//		            Log.d("wifileyanwu", scanResult.SSID+" ====�� "+j+"��");
	           
	                if (scanResult.SSID.equals(wifiName)) 
	                {
	                	Log.d("wifileyanwu", "Ѱ�ҵ�����wifi "+scanResult.SSID);
	                	return true;
	                }
	            }
	        }
	    //    j++;
    	//}
    	

        return false;
    }  
    
    private boolean connectionTargetWifi(){
    	
    	Log.d("wifileyanwu", "��ʼwifi����"+networkId);
    	if (wifiManager.enableNetwork(networkId, true)) 
    	{
            targetWifiEnable = true;
            Log.d("wifileyanwu", "wifi���ӳɹ�");                     
            return true;
        }else
        {
            Log.d("wifileyanwu", "wifi����ʧ��");
            return false;
        }
    } 
    
    public boolean initWifiConnection(Context context,String name,String wifiname)
    {
        wifiManager = (WifiManager) context.getSystemService(Service.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        
        Log.d("wifileyanwu", "���wifi���"+wifiInfo.getSSID()+"==========="+name);
        Log.d("wifileyanwu", "���wifi���"+wifiname+"==========="+name);
        wifiName = wifiname;
        wifiSSID = name;
        
        if ( wifiname.equals(wifiInfo.getSSID()) ) 
        {
            //return true;
        	//��Ӧ��ÿ�ζ���������һ�Σ�����֮ǰ�������Ѿ�ʧЧ
        }
             
        if ( targetWifiIsConfig() ) //���Ѽ�ס��WIFI����ƥ�� 
        {
            if ( targetWifiCanScan() ) 
            {

                return connectionTargetWifi();
            }

        }
        else  //��ȫ��ɨ�赽��WIFI����Ѱ��
        {
            if (!wifiManager.isWifiEnabled()) 
            {
                if (wifiManager.setWifiEnabled(true)) 
                {
                	Log.d("wifileyanwu", "���Կ���wifi");
                	
                	registerWifiChangeBoradCast(context);
                }else
                {
                    Log.d("wifileyanwu", "wifi����ʧ��");
                }
            }        	
        }
        
        return false;
    }  
    
    private void registerWifiChangeBoradCast(Context context){
        receiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                // WIFI����ʱ���ӵ�ָ���ȵ�
                System.out.println(wifiManager.getWifiState());
                
                if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    
                	Log.d("wifileyanwu", "wifi�����ɹ�");
                   
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
     * Description ע��WIFI״̬�ı�����㲥 
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