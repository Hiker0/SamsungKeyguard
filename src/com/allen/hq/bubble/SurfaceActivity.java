package com.allen.hq.bubble;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;

import com.allen.hq.R;
import com.allen.hq.bubble.BubbleSurface.BubbleCallBack;

public class SurfaceActivity extends Activity {
    private Context mContext=null;
	private BubbleSurface mView;
	private LinearLayout surfaceroot = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	      this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		
		mContext = this;
		// mView = new BasicGLSurfaceView(getApplication());
		mView = new BubbleSurface(mContext);
		mView.SetCallBack(new BubbleCallBack(){

			public void onTrigger() {
				// TODO Auto-generated method stub
				try {
					SurfaceActivity.this.finish();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


			@Override
			public void onReady() {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		 this.setContentView(R.layout.fullscreen);
		
		Animation rotateAnimation = new RotateAnimation(0f, 360f);  
        rotateAnimation.setDuration(1000);  
        
		surfaceroot = (LinearLayout) this.findViewById(R.id.surfaceroot);
 
		

		
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
		mView.onResume();
		super.onResume();

		
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
