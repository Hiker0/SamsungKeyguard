package com.allen.hq.bubble;

import java.util.ArrayList;
import java.util.Random;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.allen.hq.R;
import android.graphics.PorterDuff;

public class BubbleSurface extends SurfaceView 
			implements SurfaceHolder.Callback {
	private static final String TAG = "BubbleSurface";
	private static final int    FORKMILLIS = 1000; 
	private static final int    UNLOCKMILLIS = 700; 
	
	private SurfaceHolder mHolder=null; 
	private DrawThread drawThread;
	private Bitmap backgroundImage;
	private Rect   Drawrect = null;
	private ArrayList<Bubble> mBubbleList;

	private Point orignPoint,curPoint;

	private BubbleCallBack mCallback;
	private SoundPool mSoundPool = null;
	private int[] sounds = null;
	private Random random =new Random();
	private Context mContext = null;
	private boolean surfaceReady = false;
	private boolean unLocking    = false;
	
	Handler mHandle = new Handler();
	
	Runnable unLockRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mCallback != null){
				Log.d(TAG,"Callback ");
				clearBubble();
				drawThread.stoped=true;
				mCallback.onTrigger();
			}
		}
		
	};
	

	class DrawThread extends Thread {
		public boolean stoped = false;

		public DrawThread() {
			Log.d(TAG, "new DrawThread");
		}

		@Override
		public void run() {
			Log.d(TAG, "Thread start run");
			// TODO Auto-generated method stub
			while (!stoped) {
				synchronized (mHolder) {
					try {
						Canvas c = mHolder.lockCanvas();
						// invalidate();
						doDraw(c);
						mHolder.unlockCanvasAndPost(c);
						Thread.sleep(20);
					} catch (Exception e) {
						Log.d(TAG, "Thread error");
					}// end try
				} // end synchronized
			}// end while
		}// end run
	}
	
	class Bubble {
		
		public float x,y,z;
		public int color;
		public int alpha;
		public float speedx;
		public float speedy;
		public float acceleratX;
		public float acceleratY;
		public int life;
		public float alphaTip;
		public int size;
		
		public String toString(){
			
			
			return "{ ++++++Bubble+++++++++++\n" 
					+"x:"+x+" y"+y+" z"+z+"\n"
					+"color:"+ color+"\n"
					+"alpha:"+ alpha+"\n"
					+"speedx:"+ speedx+"\n"
					+"speedy:"+ speedy+"\n"
					+"acceleratX:"+ acceleratX+"\n"
					+"acceleratY:"+ acceleratY+"\n"
					+"life:"+ life+"\n"
					+"alphaTip:"+ alphaTip+"\n"
					+"size:"+ size+"\n"
					+"+++++++++++++++++++++++}"
					;
			
		}
		
	}

	public interface BubbleCallBack{
		
      public void onTrigger();
      public void onReady();
	}
	
	public void SetCallBack(BubbleCallBack callback){
		
		mCallback = callback;
	}
	
	
	@SuppressWarnings("serial")
	public BubbleSurface(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mHolder=getHolder();
		setZOrderOnTop(true);
		mHolder.setFormat(PixelFormat.TRANSPARENT);
		mHolder.addCallback(this);
		mBubbleList = new ArrayList<Bubble>(){};
		mContext = context;
		setSound();

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
				Log.d(TAG,"surfaceChanged ");
		// TODO Auto-generated method stub
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//drawThread.stoped = true;
		Log.d(TAG,"surfaceDestroyed ");
		onStop();
		surfaceReady = false;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG,"surfaceCreated ");
		Drawrect = holder.getSurfaceFrame();
		setSound();
		backgroundImage = createBitmap();
		
		mCallback.onReady();
        	if(drawThread == null){
			drawThread = new DrawThread();
			drawThread.start();
		}
        surfaceReady = true;
        unLocking    = false;
		
	}
	private void clearBubble(){
		synchronized(mBubbleList){
			mBubbleList.clear();
		}
	}
	private void  createBubbles(int x, int y,int number){
		
		for(int i = 0;i < number; i++){
			createBubble(x,y);
		}
		
	}
	private  void createBubble(int x, int y){
		synchronized(mBubbleList){
			int size = mBubbleList.size();	
			if(size > 450){
				return;
			}
			int type = random.nextInt(15);
			Bubble bubble = createBubble(x,y,type);
			mBubbleList.add(bubble);
		}
	}
	
	private Bubble  createBubble(float x, float y ,int type){
		
		Bubble mBubble = new Bubble();
		
		mBubble.color = getColor(backgroundImage, (int)x, (int)y);
		
		mBubble.alpha = 0xff;
		
		mBubble.life = 50 + random.nextInt(50);
		
		mBubble.alphaTip = 1.0f * mBubble.alpha / mBubble.life;
		
		//Log.d(TAG,"alpha:"+mBubble.alpha+ " life:" + mBubble.life + "alphaTip:"+ mBubble.alphaTip);
		int size;
		
		if(type == 1){
			size = 15 +  random.nextInt(15);
		}else if(type >13){
			size = 2 +  random.nextInt(5);
		}else{
			size = 4 +  random.nextInt(8);
		}
		
		mBubble.size = size;
		
		mBubble.x = x+random.nextInt(30);
		mBubble.y = y+random.nextInt(30);
		
		mBubble.speedy = (float) (0.1f * random.nextInt(80) - 1.0f);

		mBubble.speedx = (float) (3.0f-0.1f * random.nextInt(60));
		
		mBubble.acceleratY = 0.35f-0.1f*random.nextInt(5);
		mBubble.acceleratX = 0.25f-0.1f*random.nextInt(5);
		return mBubble;
	}

	
	private void doDraw(Canvas canvas){
		super.draw(canvas);
		//Log.d(TAG,"doDraw");
		Paint p = new Paint(); 
		canvas.drawBitmap(backgroundImage, 0, 0, p);
		//canvas.drawBitmap(bubbleImage, 0, 0, p);
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		Paint paint = new Paint(); 
		paint.setStyle(Paint.Style.FILL);
		synchronized(mBubbleList){
			for(int i = mBubbleList.size()-1;i>=0; i--){
				Bubble bubble = mBubbleList.get(i);
				drawBubble(bubble,canvas,paint);
			}
		}
		
	}
	
	private void drawBubble(Bubble bubble, Canvas canvas, Paint paint){
		
		bubble.life -= 1;
		
		if(bubble.life < 0){
			mBubbleList.remove(bubble);
			return;
		}

		int alpha = (int) (bubble.alpha - bubble.alphaTip);
		if(alpha <= 0){
			bubble.life = -1;
			return;
		}
		
		float speedy = (float) (bubble.speedy+bubble.acceleratY);
		float speedx = (float) (bubble.speedx+bubble.acceleratX);
		
		bubble.y-=speedy;
		bubble.x-=speedx;
		
		if(bubble.y < 0 || bubble.x < 0){
			
			bubble.life = -1;
			return;
		}
		
		paint.setColor(bubble.color);
		bubble.alpha = alpha;
		
		paint.setAlpha(alpha);
		paint.setAntiAlias(true);
		
		canvas.drawCircle(bubble.x, bubble.y, bubble.size, paint);
		//Log.d(TAG ,bubble.toString());
			
	}
	
	 
	

	
	private void setSound() {

		if (mSoundPool == null) {
			mSoundPool = new SoundPool(10, 1, 0);
			sounds = new int[3];
			sounds[0] = mSoundPool.load(mContext,
					R.raw.particle_tap, 1);
			sounds[1] = mSoundPool.load(mContext,
					R.raw.particle_unlock, 1);
			sounds[2] = mSoundPool.load(mContext,
					R.raw.particle_drag, 1);
		}
	}
	
	private int streamID = 0;
	private void playSound(int paramInt) {
		ContentResolver localContentResolver = mContext.getContentResolver();
		int j = Settings.System.getInt(localContentResolver, "lockscreen_sounds_enabled",0);
    
		AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
      int RingerMode = audio.getRingerMode();
      if(RingerMode == AudioManager.RINGER_MODE_SILENT 
              || RingerMode == AudioManager.RINGER_MODE_VIBRATE || j != 1){
          return;
      }
      
      int max = audio.getStreamMaxVolume( AudioManager.STREAM_VOICE_CALL );
      int current = audio.getStreamVolume( AudioManager.STREAM_VOICE_CALL );
//      Log.d(TAG, "max : " + max + " current : " + current);
  
      	float vol = (float)current/max * 0.5f;
//      	Log.d(TAG, "vol : " + vol );
		if (mSoundPool != null){
			//mSoundPool.stop(streamID);
			streamID = mSoundPool.play(sounds[paramInt], vol,
					vol, 0, 0, 1.0F);
		}
		
	}
	
	private void releaseSound() {
		
		if (mSoundPool != null){
			mSoundPool.release();
			mSoundPool = null;
		}
	}
	
	boolean  timeOut = false;
	final int SOUND_TYPE_TAP  = 0;
	final int SOUND_TYPE_DRAG = 1;
	final int SOUND_TYPE_UP = 2;
	
	Runnable tapTimeout = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			timeOut = true;			
		}
		
	};
	
	class SoundRunnable implements Runnable{
		private int Id = 0;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			playSound(Id);	
		}
		
		public void setId(int id){
			Id = id;
		}
		
	};
	
	SoundRunnable soundRunnable =new SoundRunnable();
	
	
   private void playTap(int type){
	  //Log.d(TAG, "type : " + type +"   timeOut:" + timeOut);
	  switch(type){
		  case SOUND_TYPE_TAP:
		  {
			  timeOut = false;
			  mHandle.removeCallbacks(tapTimeout);
			  mHandle.postDelayed((tapTimeout), FORKMILLIS);
			  mHandle.removeCallbacks(soundRunnable);
			  soundRunnable.setId(0);
			  mHandle.post(soundRunnable);
			  createBubbles(curPoint.x,curPoint.y,40);
			  break;
		  }
		  case SOUND_TYPE_DRAG:
			  if( timeOut){
				  timeOut = false;
				  mHandle.removeCallbacks(soundRunnable);
				  soundRunnable.setId(2);
				  mHandle.post(soundRunnable);
				  mHandle.removeCallbacks(tapTimeout);
				  mHandle.postDelayed((tapTimeout), FORKMILLIS);
				  createBubbles(curPoint.x,curPoint.y,40); 
			  }else{
				  createBubble(curPoint.x,curPoint.y);
			  }
			  
			  break;
		  case SOUND_TYPE_UP:
			  timeOut = false;
			  mHandle.removeCallbacks(tapTimeout);
			  break;
			  
		  default:
			  timeOut = false;
			  mHandle.removeCallbacks(tapTimeout);
	  }

	  
   }
	
	
	
	
    public  Bitmap createBitmap() {
    	
    	Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.s5_wallpaper);
        int x, y;
       
        x = Math.abs(bitmap.getWidth() - Drawrect.width()) / 2;
        y = Math.abs(bitmap.getHeight() - Drawrect.height()) / 2;

        if(x > 0 || y > 0) {
            Bitmap bitmap2 = Bitmap.createBitmap( bitmap, x, y, Drawrect.width(),Drawrect.height());
            bitmap.recycle();
            return bitmap2;
        }

        return bitmap;
    }
	public int getColor(Bitmap bitmap,int x,int y){
		
		final int COVER = 0XFF; 
		Random r  = new Random();
		int transParency = 30 + r.nextInt(20);
		int tc,rc,gc,bc,color;
		
		int rgbPixel = Color.WHITE;
		if(bitmap != null){
			rgbPixel = bitmap.getPixel(x, y);
		}
//		Log.d(TAG, "rgbPixel"+Integer.toHexString(rgbPixel));
		tc = (rgbPixel & 0xff000000);
		rc = (rgbPixel & 0x00ff0000)>>>16;
		gc = (rgbPixel & 0x0000ff00)>>8;
		bc = (rgbPixel & 0x000000ff);
		
//		Log.d(TAG, "sr:tc["+Integer.toHexString(tc)+"]"+
//				"rc["+Integer.toHexString(rc)+"]"+
//				"gc["+Integer.toHexString(gc)+"]"+
//				"bc["+Integer.toHexString(bc)+"]"
//		);
		
		rc = (rc*(100-transParency)+COVER*transParency)/100;
		gc = (gc*(100-transParency)+COVER*transParency)/100;
		bc = (bc*(100-transParency)+COVER*transParency)/100;
		
//		Log.d(TAG, "sr:tc["+Integer.toHexString(tc)+"]"+
//				"rc["+Integer.toHexString(rc)+"]"+
//				"gc["+Integer.toHexString(gc)+"]"+
//				"bc["+Integer.toHexString(bc)+"]"
//		);
	
		
		color=tc | (rc<<16)|(gc<<8)|bc;
		
//		Log.d(TAG, "color:"+Integer.toHexString(color));
		
		return color;
		
	}
//	public void setSoundRID(int tap_id, int unlock_id) {
//		mSound_tap_id = tap_id;
//		mSound_unlock_id = unlock_id;
//	}
	private void onStop(){
		Log.d(TAG,"onStop");
		
		mHandle.removeCallbacks(tapTimeout);
		mHandle.removeCallbacks(unLockRunnable);
		drawThread.stoped=true;
		drawThread = null;
				
		releaseSound();
		backgroundImage.recycle();
		clearBubble();
		
	}
	
	public void onResume(){
		Log.d(TAG,"onResume drawThread="+drawThread);

	}	
	
	
	public void onScreenTurnOn(){
		Log.d(TAG,"onScreenTurnOn");
		clearBubble();
		createBubbles(Drawrect.right/2 , Drawrect.bottom/2, 40);
	
	}
	
	private void unlockBubble(){
		Log.d(TAG,"unlockBubble");
		unLocking = true;
		mHandle.postDelayed(unLockRunnable,UNLOCKMILLIS);
	    mHandle.removeCallbacks(soundRunnable);
	    soundRunnable.setId(1);
	    mHandle.post(soundRunnable);
		  
		synchronized(mBubbleList){
		
			for(int i = mBubbleList.size()-1;i>0; i--){
				Bubble bubble = mBubbleList.get(i);
				bubble.speedy = 4 * bubble.speedy;
				bubble.speedx = 4 * bubble.speedx;
				//bubble.alphaTip   = bubble.alphaTip * 4;
				if(bubble.life > 30){
					bubble.life = 30;
					bubble.alphaTip = 1.0f * bubble.alpha /bubble.life;
				}
				
			}
		}
		//handle.postDelayed(runnable,300);
	} 
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		handleTouchEvent(event);
		return true;
	}

	public boolean handleTouchEvent(MotionEvent event) {
		if(surfaceReady && !unLocking){
	
			float x = event.getRawX();
			float y = event.getRawY();
	
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
	
				if (orignPoint == null) {
					orignPoint = new Point();
				}
				if (curPoint == null) {
					curPoint = new Point();
				}
				orignPoint.x = (int) x;
				orignPoint.y = (int) y;
				curPoint.x = (int) x;
				curPoint.y = (int) y;
	
				playTap(SOUND_TYPE_TAP);
	
				break;
			case MotionEvent.ACTION_MOVE:
				int dx = (int) Math.abs(x - curPoint.x);
				int dy = (int) Math.abs(y - curPoint.y);
				if (dx > 10 || dy > 10) {
					curPoint.x = (int) x;
					curPoint.y = (int) y;
					
					dx = Math.abs(curPoint.x - orignPoint.x);
					dy = Math.abs(curPoint.y - orignPoint.y);
					if(dx > Drawrect.width()*2/3 || dy > Drawrect.height()/4){							
						unlockBubble();
					}else{
	
						playTap(SOUND_TYPE_DRAG);
					}
	
				}
				break;
			case MotionEvent.ACTION_UP:
				curPoint.x = (int) x;
				curPoint.y = (int) y;
				playTap(SOUND_TYPE_UP);
				
				break;
			}
		}else{
			Log.d(TAG,"surfaceReady:"+surfaceReady + "\t unLocking:"+unLocking);
		}
		return true;
	}




}
