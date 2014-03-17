package com.allen.hq.keygurad;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.allen.hq.R;
import com.android.internal.policy.impl.keyguard.sec.WaterBrushView;
import com.android.internal.policy.impl.keyguard.sec.WaterBrushView.OnTriggerListener;

public class HQLockServer extends Service {
    private final String TAG = "WaterEffect";
    
    private Context mContext=null;
    
	private  WindowManager mWM = null;
	private  WindowManager.LayoutParams  wp = null;
    
	private WaterBrushView water= null;
	private LinearLayout surfaceroot = null;
	private FrameLayout root = null;
	

	
    private OnTriggerListener mOnTriggerListener;

	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mContext = this;
		 inSertWindow();
		 
		 
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mWM.removeView(root);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	protected void inSertWindow(){
		
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		
		root =(FrameLayout) inflater.inflate(R.layout.lock_screen, null);
		surfaceroot = (LinearLayout) root.findViewById(R.id.water_root);
        mWM = (WindowManager) getApplicationContext()  
                .getSystemService(Context.WINDOW_SERVICE);
        wp = new WindowManager.LayoutParams();  
        
     // …Ë÷√window type  
        wp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //wp.customtype = 1;
        wp.format = PixelFormat.RGBA_8888; 
        wp.height = 1280;
        wp.width  = 720;
        
        //wp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|WindowManager.LayoutParams.FLAG_FULLSCREEN; 
        wp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS ;
        mWM.addView(root, wp);
        
		
		water = new WaterBrushView(this);
		mOnTriggerListener = new OnTriggerListener(){

			@Override
			public void onTrigger() {
				// TODO Auto-generated method stub
				Log.d(TAG, "onTrigger");
				HQLockServer.this.stopSelf();
			}
			@Override
			public void onReady() {
				// TODO Auto-generated method stub
				
			}
			
		};
		water.setOnTriggerListener(mOnTriggerListener);
		
		surfaceroot.addView(water);

        
		water.show();
		
		root.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				water.handleTouchEvent(event);
			
				return false;
			}
		});
	}

}
