package com.android.internal.policy.impl.keyguard.sec;

import android.graphics.Bitmap;

public class JniWaterRippleRender
{
  static
  {
    System.loadLibrary("WaterRipple");
  }

  public static native void clearInkValue();

  public static native int getClearInkValue();

  public static native void initWaters(float[] paramArrayOfFloat, short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);

  public static native int move(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean, float paramFloat1, float paramFloat2);

  public static native void onDraw(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float paramFloat9, float paramFloat10);

  public static native void onDrawGravity(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, short[] paramArrayOfShort, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float paramFloat9, float paramFloat10);

  public static native void onFreeBGTextures();

  public static native void onFreeGravityTextures();

  public static native void onFreeWaterTextures();

  public static native void onInitGPU();

  public static native void onInitGPUGravity();

  public static native void onInitSetting(int paramInt1, int paramInt2, boolean paramBoolean);

  public static native void onLoadBGTextures();

  public static native void onLoadGravityTextures();

  public static native void onLoadWaterTextures();

  public static native void onTouch(int paramInt1, int paramInt2, int paramInt3, float paramFloat);

  public static native void ripple(float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, float paramFloat2, float paramFloat3);

  public static native void transferBGBitmap(Bitmap paramBitmap);

  public static native void transferGravityBitmap(Bitmap paramBitmap1, Bitmap paramBitmap2);

  public static native void transferWaterBitmap(Bitmap paramBitmap);
}

/* Location:           E:\adt-bundle-windows-x86-20130219\decode\dex2jar-0.0.9.9\policy_dex2jar.jar
 * Qualified Name:     com.android.internal.policy.impl.keyguard.sec.JniWaterRippleRender
 * JD-Core Version:    0.5.4
 */