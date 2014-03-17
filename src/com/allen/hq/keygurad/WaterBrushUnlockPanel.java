/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allen.hq.keygurad;

import com.android.internal.policy.impl.keyguard.sec.WaterBrushView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;


public class WaterBrushUnlockPanel extends FrameLayout{
  private WaterBrushView mHalo;
  public WaterBrushUnlockPanel(Context paramContext)
  {
    super(paramContext);
  }

  public WaterBrushUnlockPanel(Context paramContext, AttributeSet attrs)
  {
    super(paramContext, attrs);
  }
  public void setWaterbrush(WaterBrushView halo){
		mHalo = halo;
  }
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		Log.d("doom-lockscreen", "UnlockPanel ontouchevent action:"+action);	
		Log.d("doom-lockscreen", "UnlockPanel ontouchevent mHalo:"+mHalo);			
		if(mHalo != null){
			return mHalo.handleTouchEvent(event);
		}
		return true;//super.onTouchEvent(event);
	}	
}


