package com.test.openglfirst;


import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity1 extends AppCompatActivity {

	private static final float MIN_MOVE = 1;
	private MyGestureListener mgListener;
	private GestureDetector mDetector;
	private final static String TAG = "MyGesture";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//实例化GestureListener与GestureDetector对象
		mgListener = new MyGestureListener();
		mDetector = new GestureDetector(this, mgListener);



	}

	//自定义一个GestureListener,这个是View类下的，别写错哦！！！
	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {
			if(e1.getY() - e2.getY() > MIN_MOVE){
//				startActivity(new Intent(MainActivity.this, MainActivity.class));
				Toast.makeText(MainActivity1.this, "通过手势启动Activity", Toast.LENGTH_SHORT).show();
			}else if(e1.getY() - e2.getY()  < MIN_MOVE){
				finish();
				Toast.makeText(MainActivity1.this,"通过手势关闭Activity",Toast.LENGTH_SHORT).show();
			}
			return true;
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mDetector.onTouchEvent(event);
	}
//
//	//自定义一个GestureListener,这个是View类下的，别写错哦！！！
//	private class MyGestureListener implements GestureDetector.OnGestureListener {
//
//		@Override
//		public boolean onDown(MotionEvent motionEvent) {
//			Log.d(TAG, "onDown:按下");
//			return false;
//		}
//
//		@Override
//		public void onShowPress(MotionEvent motionEvent) {
//			Log.d(TAG, "onShowPress:手指按下一段时间,不过还没到长按");
//		}
//
//		@Override
//		public boolean onSingleTapUp(MotionEvent motionEvent) {
//			Log.d(TAG, "onSingleTapUp:手指离开屏幕的一瞬间");
//			return false;
//		}
//
//		@Override
//		public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
//			Log.d(TAG, "onScroll:在触摸屏上滑动");
//			return false;
//		}
//
//		@Override
//		public void onLongPress(MotionEvent motionEvent) {
//			Log.d(TAG, "onLongPress:长按并且没有松开");
//		}
//
//		@Override
//		public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
//			Log.d(TAG, "onFling:迅速滑动，并松开");
//			return false;
//		}
//	}



}

