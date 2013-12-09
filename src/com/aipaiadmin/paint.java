package com.aipaiadmin;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class paint extends View 
{
	private Paint mPaint = null; 
	private Paint fontPaint = null;
	private Bitmap myBitmap = null;

	
	private Path path;
	
	
	private float startX;
	private float endX;
	private float endY;
	private float startY;
	
	public int uid;
	public int gid;
	public String tid;	
	
	public int clear=0;
	
	public Context mycoContext;
	
	private Canvas mycanvas;
	
	public paint ( Context context)
	{	
		super(context);
		mycoContext = context;
		//开启线程
//		new Thread(this).start();
		mPaint = new Paint();
		path   = new Path(); 
		mPaint.setStrokeWidth(3);
		mPaint.setColor(Color.BLUE);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);	
		
		fontPaint = new Paint();
		fontPaint.setColor(Color.RED);
		fontPaint.setTextSize(40);
		
	}
	
	public void setValue(int uid,int gid,String tid)
	{
		this.uid = uid;
		this.gid = gid;
		this.tid = tid;
	}
	
	public static  String setBase64(byte[] str)
	{		

//		return  org.apache.commons.codec.binary.Base64.encodeBase64String(str);
		return "123";
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		endX = (int)event.getX();
		endY = (int )event.getY();

		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			

			path.moveTo(endX, endY); 
			startX = endX;
			startY = endY;
			
			if ( endX > 10 && endX < 100 && endY >10 && endY < 80)
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				myBitmap.compress(CompressFormat.JPEG, 100, bos);
				byte[] data = bos.toByteArray();
				
				MultipartEntityBuilder entity = MultipartEntityBuilder.create();
				Log.d("http","create");
				entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				Log.d("http",String.valueOf(uid)+"=-==="+uid);
				
				entity.addTextBody("uid", String.valueOf(uid));
				entity.addTextBody("gid", String.valueOf(gid));
				entity.addTextBody("tid", String.valueOf(tid));
				entity.addBinaryBody("img", data);
				
				HttpClient httpClient    = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				
				HttpPost httpPost = new HttpPost("http://192.168.1.140/tpad.php?action=photo");
				
				HttpEntity httpEntity = entity.build();
				httpPost.setEntity(httpEntity);
				try 
				{
					Log.d("http","111");
					HttpResponse response = httpClient.execute(httpPost,localContext);
					
					BufferedReader reader =  new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));
					
					String sResponeString = reader.readLine();
					
					Toast.makeText(mycoContext,"图片保存成功", Toast.LENGTH_LONG).show();
					
					Intent myIntent = new Intent();
					myIntent.setClass(mycoContext, mainbak.class);
					myIntent.putExtra("uids", String.valueOf(uid));
					mycoContext.startActivity(myIntent);
					
					Log.d("http",sResponeString);
					
					//回到
					
				} catch (Exception e) {
					// TODO: handle exception
				}		
			}
			else if ( endX > 110 && endX < 200 && endY >10 && endY < 80 )	//清除画板内容
			{
				this.myBitmap = null;
				clear = 1;
				Toast.makeText(mycoContext,"成功擦除", Toast.LENGTH_LONG).show();
			}
			
			
			break;
	    case MotionEvent.ACTION_MOVE: 
	    	
		    final float previousX = startX;  
		    final float previousY = startY; 	
		    
	        final float dx = Math.abs(endX - previousX);  
	        final float dy = Math.abs(endY - previousY);  	    
		    
	        if (dx >= 3 || dy >= 3) 
	        {
	         
	            float cX = (endX + previousX) / 2;  
	            float cY = (endY + previousY) / 2; 
	            
	        	path.quadTo(previousX, previousY, cX, cY);
				startX = endX;
				startY = endY;
	        }
          	break; 			
		default:
			break;
		}
		
		invalidate();
//		return super.onTouchEvent(event);
		return true;
		
		
	}



	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
		
//		canvas.drawColor(Color.BLACK);

		
		int width  = getWidth();
		int height = getHeight();
		
		if ( myBitmap == null )
		{
			myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.clear), width, height, true);
		}
		
		mycanvas = new Canvas(myBitmap);
		
		if ( clear == 1 )
		{
			Bitmap temBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.clear), width, height, true);
			mycanvas.setBitmap(temBitmap);
			path.reset();
			clear = 0;
			
		}
		
//		if ( path != null )
		mycanvas.drawPath(path, mPaint);
		
		mycanvas.drawRect(10, 10, 100, 80, mPaint);
		mycanvas.drawText("保存", 15, 55, fontPaint);
		
		mycanvas.drawRect(110, 10, 200, 80, mPaint);
		mycanvas.drawText("擦除", 115, 55, fontPaint);	
		
		
		
		canvas.drawBitmap(myBitmap,0,0, mPaint);

	}
	
	
}