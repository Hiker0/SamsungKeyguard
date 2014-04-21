package com.allen.hq.keygurad;

import com.allen.hq.bubble.BubbleSurface;
import com.android.internal.policy.impl.keyguard.sec.WaterBrushView;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;

public class LockAdapter {
	Context mContext;
	UnlockCallBack callBack = null;
	BubbleSurface bubble = null;
	WaterBrushView water = null;
	int locktype  = 0;
	
	public interface UnlockCallBack{
		
	      public void onTrigger();
	      public void onReady();
	}
	
	LockAdapter(Context context){
		mContext = context;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		locktype = prefs.getInt("lockType", 0);
		
		switch(locktype){
		case 0:
		case 1:
			water = new WaterBrushView(mContext);
			break;
		case 2:
			bubble = new BubbleSurface(mContext);
			break;
		default:
			bubble = new BubbleSurface(mContext);
		}
	}
	
	public void setCallBack(UnlockCallBack callback){
		callBack = callback;
		switch(locktype){
		case 0:
		case 1:
			water.setOnTriggerListener(callback);
			break;
		case 2:
			bubble.SetCallBack(callback);
			break;
		default:
			bubble.SetCallBack(callback);
		}
	}
	
	public void onScreenTurnOn(){
		switch(locktype){
		case 0:
		case 1:
			water.screenTurnedOn();
			break;
		case 2:
			bubble.onScreenTurnOn();
			break;
		default:
			bubble.onScreenTurnOn();
		}
	}
	
	public void setShowWhenLock(boolean show){
		
	}
	public void onResume(){
		switch(locktype){
		case 0:
		case 1:
			water.onResume();
			break;
		case 2:
			bubble.onResume();
			break;
		default:
			bubble.onResume();
		}
	}
	public void setVisibility(int visible){
		switch(locktype){
		case 0:
		case 1:
			water.setVisibility(visible);
			break;
		case 2:
			bubble.setVisibility(visible);
			break;
		default:
			bubble.setVisibility(visible);
		}
	}
	
	public int getLockType(){
		
		return locktype;
	}
	
	public View getView(){
		View view = null;
		switch(locktype){
			case 0:
			case 1:
				view = water;
				break;
			case 2:
				view = bubble;
				break;
			default:
				view = bubble;
		}
		
		return view;
	}
	
	public boolean handleTouchEvent(MotionEvent event){
		switch(locktype){
		case 0:
		case 1:
			water.handleTouchEvent(event);
			break;
		case 2:
			bubble.handleTouchEvent(event);
			break;
		default:
			bubble.handleTouchEvent(event);
		}
		return true;
	}
	
}
