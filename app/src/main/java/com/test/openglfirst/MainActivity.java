package com.test.openglfirst;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.test.openglfirst.GLUtils.GLShape.GLCamera;
import com.test.openglfirst.GLUtils.GLShape.GLCameraX;
import com.test.openglfirst.GLUtils.GLShape.GLCube;
import com.test.openglfirst.GLUtils.GLShape.GLEarth;
import com.test.openglfirst.GLUtils.GLShape.GLGrid;
import com.test.openglfirst.GLUtils.GLShape.GLLine;
import com.test.openglfirst.GLUtils.GLShape.GLMoon;
import com.test.openglfirst.GLUtils.GLShape.GLPerson;
import com.test.openglfirst.GLUtils.GLShape.GLPointCloud;
import com.test.openglfirst.GLUtils.GLShape.GLRectangle;
import com.test.openglfirst.GLUtils.GLShape.GLTK;
import com.test.openglfirst.GLUtils.GLShape.GLTable;
import com.test.openglfirst.GLUtils.GLShape.GLTable2;
import com.test.openglfirst.GLUtils.GLShape.GLTableCopy;
import com.test.openglfirst.GLUtils.GLShape.GLTextureRectangle;
import com.test.openglfirst.GLUtils.GLShape.GLTriangle;
import com.test.openglfirst.GLUtils.GLShape.MatrixState;
import com.test.openglfirst.GLUtils.GLUtil;
import com.test.openglfirst.GLUtils.base.SurfaceViewHelper;
import com.test.openglfirst.GLUtils.cameraUtils.KitkatCamera;

import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;

import java.io.IOException;
import java.nio.IntBuffer;


/**
 * @author shunlihan
 */
public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

	private static final float DISTANCE = 1;
	private static final float VELOCITY = 1;
	GLSurfaceView gl_view;

	private Session session;

	GLTriangle glTriangle = new GLTriangle();
//
	GLRectangle glRectangle= new GLRectangle();
	GLTableCopy glTableCopy = new GLTableCopy();

	float v1,v2,v3;




	GLCube glCube = new GLCube();
	GLTextureRectangle glTextureRectangle = new GLTextureRectangle();
	GLMoon glMoon = new GLMoon();
	GLTK gltk = new GLTK();

	GLEarth glEarth = new GLEarth();


	GLTable glTable = new GLTable();

	GLTable2 glTable2 = new GLTable2();
	GLPointCloud glPointCloud = new GLPointCloud();

	GLCamera glCamera;
	GLLine glLine = new GLLine();

	GLGrid glGrid = new GLGrid();

	GLPerson glPerson = new GLPerson();


	SurfaceViewHelper surfaceViewHelper = new SurfaceViewHelper();


	TouchHelper touchHelper;




	private static final String TAG = "ZoomImageView";
	public static final float SCALE_MAX = 3.0f;
	private static final float SCALE_MID = 1.5f;

	/**
	 * 初始化时的缩放比例，如果图片宽或高大于屏幕，此值将小于0
	 */
	private float initScale = 1.0f;
	private boolean once = true;

	/**
	 * 用于存放矩阵的9个值
	 */
	private final float[] matrixValues = new float[9];

	/**
	 * 缩放的手势检测
	 */
	private ScaleGestureDetector mScaleGestureDetector = null;
	private final Matrix mScaleMatrix = new Matrix();

	/**
	 * 用于双击检测
	 */
	private GestureDetector mGestureDetector;
	private boolean isAutoScale;

	private int mTouchSlop;

	private float mLastX;
	private float mLastY;

	private boolean isCanDrag;
	private int lastPointerCount;

	private boolean isCheckTopAndBottom = true;
	private boolean isCheckLeftAndRight = true;


	private int oldX1 = 0, oldX2 = 0, oldY1 = 0, oldY2 = 0, newX1 = 0, newY1 = 0, newX2 = 0, newY2 = 0, mScrollPointerId1, mScrollPointerId2;
	private boolean hasTwoFinger = false;

	public static SphericalCoordinatesUtil spherical;

	public SeekBar seekBar1,seekBar2,seekBar3;
	public TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		gl_view=findViewById(R.id.gl_view);
		textView = findViewById(R.id.tv);
//		gl_view.setLongClickable(true);

//		mGestureDetector = new GestureDetector(this);

		touchHelper = new TouchHelper(this);


		spherical = new SphericalCoordinatesUtil();

//		gl_view.setOnTouchListener();
//		gl_view.setOnTouchListener(new View.OnTouchListener() {
//			float lastPosX;
//			float lastPosY;
//			float mCurPosX;
//			float mCurPosY;
//
//			float tempX;
//			float tempY;
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//
//				switch (event.getAction()) {
//
//					case MotionEvent.ACTION_DOWN:
//						lastPosX = event.getX();
//						lastPosY = event.getY();
//						break;
//					case MotionEvent.ACTION_MOVE:
//						if (event.getPointerCount() >= 2){
//							break;
//						}
//						mCurPosX = event.getX();
//						mCurPosY = event.getY();
//
//						tempX=lastPosX-mCurPosX;
//						tempY=lastPosY-mCurPosY;
//
//						spherical.ceta += tempX*0.5;
//						spherical.fai += tempY*0.5;
//
//						lastPosX=mCurPosX;
//						lastPosY=mCurPosY;
//
//
//						Log.e("hanshunli","当前坐标ceta"+spherical.ceta+" "+mCurPosY);
//						glCube.spherical = spherical;
//
//						break;
//					case MotionEvent.ACTION_UP:
////						if (mCurPosY - mPosY > 0
////								&& (Math.abs(mCurPosY - mPosY) > 25)) {
////							//向下滑動
////
////						} else if (mCurPosY - mPosY < 0
////								&& (Math.abs(mCurPosY - mPosY) > 25)) {
////							//向上滑动
////
////
////						}
//
//						break;
//				}
//
//				return scaleGestureDetector.onTouchEvent(event);
//			}
//
//
//
//
//		});
//
////		.
//		setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				return mGestureDetector.onTouchEvent(event); //此处不作任何触摸处理，转去手势识别对象进行手势分析
//			}
//		});




//		gl_view.
		//初始化变换矩阵
		MatrixState.setInitStack();

		seekBar1 = findViewById(R.id.seekBar1);
		seekBar2 = findViewById(R.id.seekBar2);
		seekBar3 = findViewById(R.id.seekBar3);
//		v1=seekBar1.getProgress();
//		v2=seekBar2.getProgress();
//		v3=seekBar3.getProgress();
		seekBar1.setOnSeekBarChangeListener(this);
		seekBar2.setOnSeekBarChangeListener(this);
		seekBar3.setOnSeekBarChangeListener(this);


		initCamera();

		glCamera = new GLCamera(gl_view,textureId,kitkatCamera);

		GLCameraX glCameraX = new GLCameraX(this);
		surfaceViewHelper.addRender(glCamera);

		surfaceViewHelper.addRender(glGrid);


//		surfaceViewHelper.addRender(glTriangle);

		surfaceViewHelper.addRender(glPointCloud);
//		surfaceViewHelper.addRender(glTableCopy);
//
		surfaceViewHelper.addRender(glLine);

		surfaceViewHelper.addRender(glPerson);
//		surfaceViewHelper.addRender(glTable);




//		surfaceViewHelper.addRender(glTable2);
//

//
//		surfaceViewHelper.addRender(glCameraX);
//		surfaceViewHelper.addRender(glCube);

//		surfaceViewHelper.addRender(glTriangle);
//		surfaceViewHelper.addRender(glMoon);
//		surfaceViewHelper.addRender(glTextureRectangle);



//		surfaceViewHelper.addRender(glRectangle);


//		verifyStoragePermissions(this);
//		try {
//			read();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		surfaceViewHelper.init(gl_view);
	}


	/**
	 * Fill the index buffer with the data from the PLY file.
	 */
	private static void fillIndexBuffer(
			ElementReader reader,
			IntBuffer indexBuffer) throws IOException {

		// Just go though the triangles and store the indices in the buffer.
		Element triangle = reader.readElement();
		while (triangle != null) {

			int[] indices = triangle.getIntList("vertex_index");
			for (int index : indices) {
				indexBuffer.put(index);
			}

			triangle = reader.readElement();
		}
	}


	//先定义
	private static final int REQUEST_EXTERNAL_STORAGE = 1;

	private static String[] PERMISSIONS_STORAGE = {
			"android.permission.READ_EXTERNAL_STORAGE",
			"android.permission.WRITE_EXTERNAL_STORAGE" };

	//然后通过一个函数来申请
	public static void verifyStoragePermissions(Activity activity) {
		try {
			//检测是否有写的权限
			int permission = ActivityCompat.checkSelfPermission(activity,
					"android.permission.WRITE_EXTERNAL_STORAGE");
			if (permission != PackageManager.PERMISSION_GRANTED) {
				// 没有写的权限，去申请写的权限，会弹出对话框
				ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}






	/////////////////////

	private boolean installRequested =false;
	@Override
	protected void onResume() {
		super.onResume();

		if (session == null) {
			Exception exception = null;
			String message = "null";
			try {
				switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
					case INSTALL_REQUESTED:
						installRequested = true;
						return;
					case INSTALLED:
						break;
				}
				// Create the session.
				session = new Session(/* context= */ this);
			} catch (UnavailableArcoreNotInstalledException e) {
				message = "Please install ARCore";
			} catch (UnavailableApkTooOldException e) {
				message = "Please update ARCore";
				exception = e;
			} catch (UnavailableSdkTooOldException e) {
				message = "Please update this app";
				exception = e;
			} catch (UnavailableDeviceNotCompatibleException e) {
				message = "This device does not support AR";
				exception = e;
			} catch (Exception e) {
				message = "Failed to create AR session";
				exception = e;
			}


			Log.e("hanshunli",message);
		}

		// Note that order matters - see the note in onPause(), the reverse applies here.
		try {
			configureSession();
			session.resume();
		} catch (CameraNotAvailableException e) {

			session = null;
			return;
		}





//		int textureId = GLUtil.loadCameraTexture();
		surfaceViewHelper.setSession(session,textureId);

//		session



		new Thread(new Runnable() {


			@Override
			public void run() {
				do{
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

//						textView.setText(String.format("qx11: %.3f" ,SaveHelper.qx11));
//						textView.append(String.format("\nqy: %.3f" ,SaveHelper.qy11));
//						textView.append(String.format("\nqz11: %.3f" ,SaveHelper.qz11));
////						textView.append(String.format("\nqw11: %.3f" ,SaveHelper.qw11));

						}
					});
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}while (true);
			}
		}).start();
	}

	int textureId;

	KitkatCamera kitkatCamera  = new KitkatCamera();
	void initCamera() {
		kitkatCamera.open(0);
		textureId = GLUtil.loadCameraTexture();
		Log.e("hansuhnli", "textureId--" + textureId);


	}


	void openCamera(){

//		kitkatCamera.setPreviewTexture(mSurfaceTexture);

//		kitkatCamera.setDisplayOrientation(90);
//		kitkatCamera.preview();

	}

	/**
	 * Configures the session with feature settings.
	 */
	private void configureSession() {
		Config config = session.getConfig();
//		if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
//			config.setDepthMode(Config.DepthMode.AUTOMATIC);
//		} else {
//			config.setDepthMode(Config.DepthMode.DISABLED);
//		}
//		if (true) {
//			config.setInstantPlacementMode(Config.InstantPlacementMode.LOCAL_Y_UP);
//		} else {
//			config.setInstantPlacementMode(Config.InstantPlacementMode.DISABLED);
//		}
		session.configure(config);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
		float i1 = i;
		i1/=100;
		switch (seekBar.getId()){
			case R.id.seekBar1:
				v1 = i1;
				glPerson.currentPersonX=i1;
				break;

			case R.id.seekBar2:
				v2 = i1;
				glPerson.currentPersonY=i1;
				break;

			case R.id.seekBar3:
				v3 = i1;
				glPerson.currentPersonAngle=i1*100;
				glPointCloud.pointSize = i1*40+ 30;
				break;

			default:
				break;


		}

//		glTable.updateCamera(v1,v2,v3);
		glLine.updateCamera(v1,v2,v3);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}


	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;

	@Override
	public boolean onTouchEvent(MotionEvent me) {


		boolean b = touchHelper.onTouchEvent(me);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView.setText("focusX:"+touchHelper.focusX);
				textView.append("\nfocusY:"+touchHelper.focusY);
				textView.append("\nmScaleFactor:"+touchHelper.mScaleFactor);

//				textView.setText("qx11:"+ SaveHelper.qx11);
//				textView.append("qy11:"+ SaveHelper.qy11);
//				textView.append("qz11:"+ SaveHelper.qz11);
//				textView.append("qw11:"+ SaveHelper.qw11);
			}
		});

//		touchHelper.mPosX/=10;
//		touchHelper.mPosY/=10;
		if (touchHelper.mPosY/10 >=179){
			touchHelper.mPosY = 1790;
		}else if (touchHelper.mPosY/10 <=1){
			touchHelper.mPosY = 10;
		}
		spherical.ceta=touchHelper.mPosX/10;
		spherical.fai=touchHelper.mPosY/10;
		spherical.r/=touchHelper.mScaleFactor;

		touchHelper.mScaleFactor=1;


		spherical.lookAtX = touchHelper.focusX /100;
		spherical.lookAtY = touchHelper.focusY /100;
		return b;
//		gestureDec.onTouchEvent(me);
//
//		if (me.getPointerCount()>=2){
//			return true;
//		}
//
//		if (me.getAction() == MotionEvent.ACTION_DOWN) {
//			xpos = me.getX();
//			ypos = me.getY();
//			return true;
//		}
//
//		if (me.getAction() == MotionEvent.ACTION_UP) {
//			xpos = -1;
//			ypos = -1;
//			touchTurn = 0;
//			touchTurnUp = 0;
//			return true;
//		}
//
//		if (me.getAction() == MotionEvent.ACTION_MOVE) {
//			float xd = me.getX() - xpos;
//			float yd = me.getY() - ypos;
//
//			xpos = me.getX();
//			ypos = me.getY();
//
//			touchTurn = xd / -100f;
//			touchTurnUp = yd / -100f;
//
//			spherical.ceta+=touchTurn*6;
//			spherical.fai+=touchTurnUp*6;
//			return true;
//		}
//
//
//
//
//
//		try {
//			Thread.sleep(15);
//		} catch (Exception e) {
//			// No need for this...
//		}
//
//		return super.onTouchEvent(me);
	}

}