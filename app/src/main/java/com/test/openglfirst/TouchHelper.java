package com.test.openglfirst;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.core.view.MotionEventCompat;

import static android.view.MotionEvent.INVALID_POINTER_ID;

class TouchHelper {

	// The ‘active pointer’ is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;

	float mLastTouchX;
	float mLastTouchY;
	float mPosX;
    float mPosY;
	float mScaleFactor =1;

	float mLastFocusX;
	float mLastFocusY;

	float focusX;
	float focusY;
	ScaleGestureDetector mScaleDetector;
	public TouchHelper(Context context){
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}

	float saveX;
	float saveY;

	public boolean onTouchEvent(MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.

		mScaleDetector.onTouchEvent(ev);


		final int action = MotionEventCompat.getActionMasked(ev);

		switch (action) {
			case MotionEvent.ACTION_DOWN: {
				final int pointerIndex = MotionEventCompat.getActionIndex(ev);
				final float x = MotionEventCompat.getX(ev, pointerIndex);
				final float y = MotionEventCompat.getY(ev, pointerIndex);

				// Remember where we started (for dragging)
				mLastTouchX = x;
				mLastTouchY = y;
				// Save the ID of this pointer (for dragging)
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				// Find the index of the active pointer and fetch its position
				final int pointerIndex =
						MotionEventCompat.findPointerIndex(ev, mActivePointerId);

				final float x = MotionEventCompat.getX(ev, pointerIndex);
				final float y = MotionEventCompat.getY(ev, pointerIndex);

				// Calculate the distance moved
				final float dx = x - mLastTouchX;
				final float dy = y - mLastTouchY;

				mPosX += dx;
				mPosY -= dy;

//				invalidate();

				// Remember this touch position for the next move event
				mLastTouchX = x;
				mLastTouchY = y;

				break;
			}

			case MotionEvent.ACTION_UP: {
				mActivePointerId = INVALID_POINTER_ID;
				break;
			}

			case MotionEvent.ACTION_CANCEL: {
				mActivePointerId = INVALID_POINTER_ID;
				break;
			}

			case MotionEvent.ACTION_POINTER_UP: {

				final int pointerIndex = MotionEventCompat.getActionIndex(ev);
				final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

				if (pointerId == mActivePointerId) {
					// This was our active pointer going up. Choose a new
					// active pointer and adjust accordingly.
					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
					mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
					mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
					mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
				}
				break;

			}
			default:
//				throw new IllegalStateException("Unexpected value: " + action);
		}

		if (ev.getPointerCount() ==1){
			saveX = mPosX;
			saveY = mPosY;

			mLastFocusX=0;
			mLastFocusY=0;

		}else if (ev.getPointerCount() ==2){
			mPosX=saveX ;
			mPosY=saveY ;

			float x = (ev.getX(0) + ev.getX(1))/2;
			float y = (ev.getY(0) + ev.getY(1))/2;


			if (mLastFocusX ==0){
				mLastFocusX = x;
			}
			if (mLastFocusY ==0){
				mLastFocusY = y;
			}



			final float dx = x - mLastFocusX;
			final float dy = y - mLastFocusY;

			focusX += dx;
			focusY -= dy;

//				invalidate();

			// Remember this touch position for the next move event
			mLastFocusX = x;
			mLastFocusY = y;


		}else if (ev.getPointerCount() >2){
			mPosX=saveX ;
			mPosY=saveY ;
		}

		return true;
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();


			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

//			invalidate();
			return true;
		}
	}

}
