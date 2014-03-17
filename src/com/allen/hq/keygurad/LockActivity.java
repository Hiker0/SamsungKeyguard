package com.allen.hq.keygurad;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;

import com.allen.hq.R;
import com.allen.hq.bubble.BubbleSurface;

public class LockActivity extends Activity {
    private Context mContext=null;
	private SurfaceView mView;
	private LinearLayout surfaceroot = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		
		mContext = this;
		// mView = new BasicGLSurfaceView(getApplication());
		mView = new BubbleSurface(getApplication());
		 this.setContentView(R.layout.lock_screen);
		
		Animation rotateAnimation = new RotateAnimation(0f, 360f);  
        rotateAnimation.setDuration(1000);  
        
		surfaceroot = (LinearLayout) this.findViewById(R.id.water_root);
 
		

		
		surfaceroot.addView(mView);

        
		
		surfaceroot.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

			
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
