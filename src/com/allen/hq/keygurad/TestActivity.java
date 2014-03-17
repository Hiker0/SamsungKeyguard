package com.allen.hq.keygurad;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.allen.hq.R;
import com.allen.hq.R.id;
import com.allen.hq.R.layout;
import com.android.internal.policy.impl.keyguard.sec.WaterBrushView;
import com.android.internal.policy.impl.keyguard.sec.WaterBrushView.OnTriggerListener;

public class TestActivity extends Activity {
    private Context mContext=null;
	private  WindowManager mWM = null;
	private  WindowManager.LayoutParams  wp = null;
    
	private WaterBrushView water= null;
	private LinearLayout surfaceroot = null;
	private FrameLayout root = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

		// mView = new BasicGLSurfaceView(getApplication());
		this.setContentView(R.layout.lock_screen);
		
		Animation rotateAnimation = new RotateAnimation(0f, 360f);  
        rotateAnimation.setDuration(1000);  
        
		surfaceroot = (LinearLayout) this.findViewById(R.id.water_root);;
 
		
		water = new WaterBrushView(this);
		OnTriggerListener mOnTriggerListener = new OnTriggerListener(){
			
			@Override
			public void onTrigger() {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onReady() {
				// TODO Auto-generated method stub
				
			}
			
			
		};
		water.setOnTriggerListener(mOnTriggerListener);
		
		surfaceroot.addView(water);

        
		water.show();
		
		surfaceroot.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				water.handleTouchEvent(event);
			
				return false;
			}
		});

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// mView.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// mView.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// root = (RelativeLayout)this.findViewById(R.id.root);
		// root.addView(water);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}
}
