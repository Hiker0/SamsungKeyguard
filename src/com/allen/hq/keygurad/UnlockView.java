package com.allen.hq.keygurad;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public abstract interface UnlockView
{
  public abstract void cleanUp();

  public abstract long getUnlockDelay();

  public abstract boolean handleHoverEvent(MotionEvent paramMotionEvent);

  public abstract boolean handleTouchEvent(View paramView, MotionEvent paramMotionEvent);

  public abstract boolean handleTouchEventForPatternLock(View paramView, MotionEvent paramMotionEvent);

  public abstract void handleUnlock(View paramView, MotionEvent paramMotionEvent);

  public abstract void playLockSound();

  public abstract void reset();

  public abstract void screenTurnedOn();

  public abstract void show();

  public abstract void showUnlockAffordance(long paramLong, Rect paramRect);
}

/* Location:           E:\adt-bundle-windows-x86-20130219\decode\dex2jar-0.0.9.9\policy_dex2jar.jar
 * Qualified Name:     com.android.internal.policy.impl.keyguard.sec.UnlockView
 * JD-Core Version:    0.5.4
 */