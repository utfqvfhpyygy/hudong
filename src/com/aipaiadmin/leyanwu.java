package com.aipaiadmin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class leyanwu extends Activity {
	
	private Button m1,m2,m3;
	private TextView t1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fail);
        
//        t1 = (TextView)findViewById(R.id.textView1);
        
//        t1.setText("µÁ‘¥");
        
       
        
        m1 = (Button)findViewById(R.id.retry);
        m2 = (Button)findViewById(R.id.exit);
        m3 = (Button)findViewById(R.id.chooseWifi1);
        
        
        m1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		        Intent intent = new Intent();
		        
		        intent.setClass(leyanwu.this, MainActivity.class);
		        startActivity(intent);
		        leyanwu.this.finish();  				
			}

        });
      
        m2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

		        leyanwu.this.finish();  	
		        System.exit(0);
			}

        });      
        
        m3.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	            Intent intent = new Intent("android.settings.WIFI_SETTINGS");
	            startActivity(intent);
	            
		        leyanwu.this.finish();  	
		        System.exit(0);
			}

        });          
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
    			break;
    		case 1:	
    			leyanwu.this.finish();
    			System.exit(0);
    			break;
    	}
    	
    	return true;
//		return super.onOptionsItemSelected(item);
	}


}
