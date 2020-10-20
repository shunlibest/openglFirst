package com.test.openglfirst;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

/**
 * A simple demo. This shows more how to use jPCT-AE than it shows how to write
 * a proper application for Android. It includes basic activity management to
 * handle pause and resume...
 *
 * @author EgonOlsen
 */
@SuppressLint("NewApi")
public class HelloWorld extends Activity {
	private int AA,BB,CC;



	// Used to handle pause and resume...
	private static HelloWorld master = null;

	private GLSurfaceView mGLView;
	private MyRenderer renderer = null;
	// 当JPCT渲染背景时FrameBuffer类提供了一个缓冲,它的结果本质上是一个能显示或者修改甚至能进行更多后处理的图片。
	private FrameBuffer fb = null;
	// World类是JPCT时最重要的一个类，它好像胶水一样把事物"粘"起来。它包含的对象和光线定义了JPCT的场景
	private World world = null;
	// 类似java.awt.*中的Color类
	private RGBColor back = new RGBColor(0, 0, 0);
	private View bottom;

	private TextView tv_camera;

	private float touchTurn = 0.0f;
	private float touchTurnUp = 0.0f;
	private float touchDown = 0;

	private float baseValue = 0;
	private float baseCameraValue = 100;
	private float scale = 0;

	private float xpos = -1;
	private float ypos = -1;
	private Object3D phone = null;// 3d模型对象
//	private Object3D phoneChildren = null;// 3d模型对象

	private Object3D plane = null;// 地板

	private Object3D cameraObj = null;// 摄像机

	private Object3D houseObj = null;// 摄像机

	private Object3D XXX = null;// 摄像机
	private Object3D YYY = null;// 摄像机
	private Object3D ZZZ = null;// 摄像机

	private Object3D CCC = null;// 摄像机


	// 每秒帧数
	private int fps = 0;
	// 光照类
	private Light sun = null;

//	private Light moon = null;
	private ProgressDialog progressDialog;
	private ScaleGestureDetector scaleGestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("加载中,请稍候...");
		progressDialog.show();
		// Logger类中 jPCT中一个普通的用于打印和存储消息，错误和警告的日志类。
		// 每一个JPCT生成的消息将被加入到这个类的队列中
		Logger.log("onCreate");
		// 如果本类对象不为NULL,将从Object中所有属性装入该类
		if (master != null) {
			copy(master);
		}

		super.onCreate(savedInstanceState);
		scaleGestureDetector=new ScaleGestureDetector(this,new SimpleOnScaleGestureListener());


		mGLView = new GLSurfaceView(getApplication());
		// 使用自己实现的 EGLConfigChooser,该实现必须在setRenderer(renderer)之前
		// 如果没有setEGLConfigChooser方法被调用，则默认情况下，视图将选择一个与当前android.view.Surface兼容至少16位深度缓冲深度EGLConfig。
		mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			@Override
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				// Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
				// back to Pixelflinger on some device (read: Samsung I7500)
				int[] attributes = new int[]{EGL10.EGL_DEPTH_SIZE, 16,
						EGL10.EGL_NONE};
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});
		renderer = new MyRenderer();
		mGLView.setRenderer(renderer);
		// 在模型下面添加底部view
		FrameLayout frameLayout = new FrameLayout(this);
		frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));

		RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.mobile_bottom, null);
		bottom = view.findViewById(R.id.parent);
		tv_camera = view.findViewById(R.id.tv_camera);
		frameLayout.addView(mGLView);
		frameLayout.addView(bottom);
		setContentView(frameLayout);

		SeekBar seekBar1 = (SeekBar) view.findViewById(R.id.seekbar11);
		seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				AA=i;
				sphericalCoordinatesUtil.ceta+=2;

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
		SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.seekbar22);
		seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				BB=i;
				sphericalCoordinatesUtil.fai+=2;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
		SeekBar seekBar3 = (SeekBar) view.findViewById(R.id.seekbar33);
		seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				CC=i;
				needTorote++;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
	}

	/**
	 * 监听3D手机底部的返回、放大、缩小、左旋转、有旋转、复位控件
	 */
	public void onBtnClick(View v) {
		switch (v.getId()) {
			case R.id.goback:
//			cube.clearAnimation();
//			cube.clearAdditionalColor();
//			cube.clearShader();
//			cube.clearObject();
				//world.removeAll();
				finish();
				break;
			case R.id.big:
				phone.scale(1.1f);
				break;
			case R.id.small:
				phone.scale(0.9f);
				break;
			case R.id.left:
				touchTurn = touchTurn + 0.08641969f;
				break;
			case R.id.right:
				touchTurn = touchTurn - 0.08641969f;
				break;
			case R.id.reset:
				phone.clearRotation();
				break;
			default:
				break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void copy(Object src) {
		try {
			// 打印日志
			Logger.log("Copying data from master Activity!");
			// 返回一个数组，其中包含目前这个类的的所有字段的Filed对象
			Field[] fs = src.getClass().getDeclaredFields();
			// 遍历fs数组
			for (Field f : fs) {
				// 尝试设置无障碍标志的值。标志设置为false将使访问检查，设置为true，将其禁用。
				f.setAccessible(true);
				// 将取到的值全部装入当前类中
				f.set(this, f.get(src));
			}
		} catch (Exception e) {
			// 抛出运行时异常
			throw new RuntimeException(e);
		}
	}
	float lastPosX = 0;
	float lastPosY = 0;
	float mCurPosX;
	float mCurPosY;

	@Override
	public boolean onTouchEvent(MotionEvent me) {

		scaleGestureDetector.onTouchEvent(me);

		if (me.getPointerCount()>=2){
			touchTurn = 0;
			touchTurnUp = 0;
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_DOWN) {
//			int actionIndex = me.getActionIndex();
			xpos = me.getX();
			ypos = me.getY();
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_UP) {
			xpos = -1;
			ypos = -1;
			touchTurn = 0;
			touchTurnUp = 0;
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			float xd = me.getX() - xpos;
			float yd = me.getY() - ypos;

			xpos = me.getX();
			ypos = me.getY();

			touchTurn = xd / -100f;
			touchTurnUp = yd / -100f;
			return true;
		}

		try {
			Thread.sleep(15);
		} catch (Exception e) {
			// No need for this...
		}

		return super.onTouchEvent(me);
	}

//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//
////		if (me.getAction() == MotionEvent.ACTION_DOWN) {
////			// 保存按下的初始x,y位置于xpos,ypos中
////			xpos = me.getX();
////			ypos = me.getY();
//////			System.out.println("ACTION_DOWN===============" + xpos);
//////			System.out.println("ACTION_DOWN===============" + ypos);
////			// touchDown = 1;
////			// baseCameraValue = world.getCamera().getSideVector().z;
////			return true;
////		}
////
////		if (me.getAction() == MotionEvent.ACTION_UP) {
////			// 设置x,y及旋转角度为初始值
//////			System.out.println("ACTION_UP===============" + xpos);
//////			System.out.println("ACTION_UP===============" + ypos);
////			xpos = -1;
////			ypos = -1;
////			touchTurn = 0;
////			touchTurnUp = 0;
////			// touchDown = 0;
////			return true;
////		}
////
////		if (me.getAction() == MotionEvent.ACTION_MOVE) {
////
////			if (me.getPointerCount() == 2) {
////				float x = me.getX(0) - me.getX(1);
////				float y = me.getY(0) - me.getY(1);
////				float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
////
////				if (baseValue == 0) {
////					baseValue = value;
////				} else {
////					if (value - baseValue >= 10 || value - baseValue <= -10) {
////						scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
////						Log.d("scale", "" + scale);
//////						cube.scale(scale);
////					}
////				}
////				return super.onTouchEvent(me);
////			}
//////			 计算x,y偏移位置及x,y轴上的旋转角度
////			float xd = me.getX() - xpos;
////			float yd = me.getY() - ypos;
////
////			xpos = me.getX();
////			ypos = me.getY();
////			// 以x轴为例，鼠标从左向右拉为正，从右向左拉为负
////			touchTurn = xd / -100f;
////			touchTurnUp = yd / -100f;
////
////			// System.out.println("ACTION_MOVE  touchTurnUp===============" +
////			// touchTurnUp);
////			return true;
////		}
////
////		try {
////			// 每Move一下休眠15毫秒
////			Thread.sleep(15);
////		} catch (Exception e) {
////		}
//
//
//
//		float tempX;
//		float tempY;
//
//
//		switch (event.getAction()) {
//
//			case MotionEvent.ACTION_DOWN:
//				lastPosX = event.getX();
//				lastPosY = event.getY();
//				break;
//			case MotionEvent.ACTION_MOVE:
//				if (event.getPointerCount() >= 2){
//					break;
//				}
//				mCurPosX = event.getX();
//				mCurPosY = event.getY();
//
//				tempX=lastPosX-mCurPosX;
//				tempY=lastPosY-mCurPosY;
//
//				Log.e("hanshunli","tempX"+tempX);
////				cube.rotateY(tempX*0.005f);
////
////				cube.rotateX(tempY*0.005f);
//
//
////				Camera camera = world.getCamera();
//
////				world.
////				camera.rotateY(tempX*0.005f);
//
////				camera.rotateAxis(new SimpleVector(0,1,0),tempX*0.005f);
//
////				camera.
//				//				spherical.ceta += tempX*0.5;
////				spherical.fai += tempY*0.5;
//
////				sphericalCoordinatesUtil.
////				lastPosX=mCurPosX;
////				lastPosY=mCurPosY;
//
//
//
//
//
//					break;
//				case MotionEvent.ACTION_UP:
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
//					break;
//			default:
////				throw new IllegalStateException("Unexpected value: " + event.getAction());
//		}
//
//
//
//
//		return true;
//
////		return super.onTouchEvent(me);
//	}

	protected boolean isFullscreenOpaque() {
		return true;
	}
	public static SphericalCoordinatesUtil  sphericalCoordinatesUtil;

	float needTorote=0;

	class MyRenderer implements GLSurfaceView.Renderer {
		// 当前系统时间的毫秒数
		private long time = System.currentTimeMillis();

		public MyRenderer() {
		}

		// 当屏幕改变时
		@Override
		public void onSurfaceChanged(GL10 gl, int w, int h) {

			// 如果FrameBuffer不为NULL,释放fb所占资源
			if (fb != null) {
				fb.dispose();
			}
			// 在使用前创建一个宽w,高h,内容为支持opengl
			fb = new FrameBuffer(gl, w, h);

			if (master == null) {
				System.out.println("onSurfaceChanged");
				world = new World();
				// 设置了环境光源强度。设置此值是负的整个场景会变暗，而为正将照亮了一切。
				world.setAmbientLight(200, 200, 200);
				// 在World中创建一个新的光源
				sun = new Light(world);
				// 设置光照强度
				sun.setIntensity(250, 250, 250);

//				moon = new Light(world);
//				moon.setIntensity(250, 250, 250);

				// Create a texture out of the icon...:-)
				// 创建一个纹理对象
				Texture samSungTexture = new Texture(getResources() .openRawResource(R.raw.s3));
				samSungTexture.setMipmap(false);
				TextureManager.getInstance().addTexture("samSungTexture",samSungTexture);

				Texture www = new Texture(getResources() .openRawResource(R.raw.www));
				www.setMipmap(false);
				TextureManager.getInstance().addTexture("www",www);

				// 载入obj and mtl
//				Loader
				phone = Loader.loadOBJ(
						getResources().openRawResource(R.raw.s3obj),
						getResources().openRawResource(R.raw.s3mtl), 0.15f)[0];
				// 给对象设置纹理
				phone.setTexture("samSungTexture");
				// 除非你想在事后再用PolygonManager修改,否则释放那些不再需要数据的内存
				phone.strip();

				// 初始化一些基本的对象是几乎所有进一步处理所需的过程。
				// 如果对象是"准备渲染"(装载，纹理分配，安置，渲染模式设置，
				// 动画和顶点控制器分配),那么build()必须被调用，
				phone.build();

//				phone.translate(0,100,100);
				// 将Object3D对象添加到world集合
				world.addObject(phone);

				///////////////////////////////////////////////////////////////

//				phoneChildren = Primitives.getCylinder(1);

//				cameraObj.setTexture("samSungTexture");
//				cameraObj.strip();
//				cameraObj.build();


				///////////////////////////////////////////////////////////////
				cameraObj = Loader.loadOBJ(
						getResources().openRawResource(R.raw.cameraobj),
						getResources().openRawResource(R.raw.cameramtl), 1f)[0];

				cameraObj.setTexture("samSungTexture");
				cameraObj.strip();
				cameraObj.build();
//				world.addObject(cameraObj);

				//////////////////////////////////////////////////////////////////////////////////////////////////


				houseObj = Loader.loadOBJ(
				getResources().openRawResource(R.raw.houseobj),
				getResources().openRawResource(R.raw.housemtl), 5f)[0];

//				houseObj.setTexture("samSungTexture");
				houseObj.strip();
				houseObj.build();
				houseObj.addParent(phone);
				world.addObject(houseObj);

				//////////////////////////////////////////////////////////////////////////////////////////////////


				plane = Primitives.getPlane(100, 10f);
				plane.rotateX(1.5707964F);
				plane.setTexture("www");

				plane.addParent(phone);
				plane.build();
				world.addObject(plane);
				plane.strip();
				//////////////////////////////////////////////////////////////////////////////////////////////////
				XXX = Primitives.getCone(100, 10f);
				YYY = Primitives.getCone(100, 10);
				ZZZ = Primitives.getCone(100, 10);

				Texture wgRed = new Texture(getResources() .openRawResource(R.raw.wgred));
				wgRed.setMipmap(false);
				TextureManager.getInstance().addTexture("wgRed",wgRed);

				Texture wggreen = new Texture(getResources() .openRawResource(R.raw.wggreen));
				wggreen.setMipmap(false);
				TextureManager.getInstance().addTexture("wggreen",wggreen);

				Texture wgblue = new Texture(getResources() .openRawResource(R.raw.wgblue));
				wgblue.setMipmap(false);
				TextureManager.getInstance().addTexture("wgblue",wgblue);

				XXX.setTexture("wgRed");
				YYY.setTexture("wggreen");
				ZZZ.setTexture("wgblue");

				XXX.translate(50,0,0);
				YYY.translate(0,50,0);
				ZZZ.translate(0,0,50);

				XXX.build();
				YYY.build();
				ZZZ.build();

				world.addObject(XXX);
				world.addObject(YYY);
				world.addObject(ZZZ);


				YYY.strip();
				ZZZ.strip();



				//////////////////////////////////////////////////////////////////////////////////////////////////
				CCC = Primitives.getCone(100, 10);
//				CCC.setTexture("wgRed");
				CCC.setAdditionalColor(100,100,1);

				CCC.build();
				world.addObject(CCC);
				CCC.strip();




				//////////////////////////////////////////////////////////////////////////////////////////////////


//				world.getCamera().align(phone);

				world.getCamera().setPosition(0,-80,0);
				world.getCamera().lookAt(new SimpleVector(0,-50,0));
//				world.set

				// 该Camera代表了Camera/viewer在当前场景的位置和方向，它也包含了当前视野的有关信息
				// 你应该记住Camera的旋转矩阵实际上是应用在World中的对象的一个旋转矩阵。
				// 这一点很重要，当选择了Camera的旋转角度，一个Camera(虚拟)围绕w旋转和通过围绕World围绕w旋转、
				// 将起到相同的效果，因此，考虑到旋转角度，World围绕camera时，camera的视角是静态的。假如你不喜欢
				// 这种习惯，你可以使用rotateCamera()方法
//				Camera camera的视角是静态的m = world.getCamera();
				// 以50有速度向后移动Camera（相对于目前的方向）
//				cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
				// 旋转这样camera以至于它看起来是在给定的world-space 的位置
//				cam.lookAt(phone.getTransformedCenter());
				// SimpleVector是一个代表三维矢量的基础类，几乎每一个矢量都
				// 是用SimpleVector或者至少是一个SimpleVector变体构成的(有时由于
				// 某些原因比如性能可能会用(float x,float y,float z)之类)。
//				SimpleVector sv = new SimpleVector();
//				sv.set(phone.getTransformedCenter());
//				sv.y -= 100;// Y方向上减去100
//				sv.z -= 100; // Z方向上减去100
//				// 设置光源位置
//				sun.setPosition(sv);
//
//				SimpleVector svmoon = new SimpleVector();
//				svmoon.set(phone.getTransformedCenter());
//				svmoon.y += 100;
//				svmoon.z -= 100;

//				moon.setPosition(svmoon);
				// 强制GC和finalization工作来试图去释放一些内存，同时将当时的内存写入日志，
				// 这样可以避免动画不连贯的情况，然而，它仅仅是减少这种情况发生的机率
				MemoryHelper.compact();

				if (master == null) {
					Logger.log("Saving master Activity!");
					master = HelloWorld.this;
				}
			}
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {

			sphericalCoordinatesUtil = new SphericalCoordinatesUtil();
			System.out.println("onSurfaceCreated");
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			System.out.println("onDrawFrame");
			// 如果touchTurn不为0,向Y轴旋转touchTure角度
//			if (touchTurn != 0) {
//				phone.rotateY(touchTurn);
//				touchTurn = 0;
//
//			}
//			if (touchTurnUp != 0) {
//				// 旋转物体的旋转围绕x由给定角度宽（弧度，逆时针为正值）轴矩阵,应用到对象下一次渲染时。
//				phone.rotateX(touchTurnUp);
//				touchTurnUp = 0;
//			}
//
//			if (scale != 0 && touchDown != 0) {
//				SimpleVector sv = world.getCamera().getSideVector();
//
//				float delta = sv.z - baseCameraValue * scale;
//
//				world.getCamera().moveCamera(new SimpleVector(0, 0, delta), 50);
//				scale = 0;
//			}


			if (touchTurn != 0) {
				Log.e("hanshunli","touchTurn:  "+touchTurn);
//				plane.rotateY(touchTurn);
//				object3Dss.rotateY(touchTurn);

				sphericalCoordinatesUtil.ceta+=touchTurn*6;

//				needTorote+=touchTurn*1;
//				cam.rotateZ(touchTurn);
//				cam.rotateCameraY(touchTurn);
//				cam.
				touchTurn = 0;
			}

			if (touchTurnUp != 0) {
//				plane.rotateX(touchTurnUp);
				Log.e("hanshunli","touchTurnUp:  "+touchTurnUp);

				sphericalCoordinatesUtil.fai+=touchTurnUp*6;
//				object3Dss.rotateX(touchTurnUp);
				touchTurnUp = 0;
			}

//			sphericalCoordinatesUtil.ceta = AA;
//			sphericalCoordinatesUtil.fai = BB;
//			phone.rotateY(needTorote);
//			needTorote=0;

//			sphericalCoordinatesUtil.ceta=0;

			sphericalCoordinatesUtil.toXYZ();
//			CCC.setOrigin(new SimpleVector(sphericalCoordinatesUtil.x,sphericalCoordinatesUtil.y,sphericalCoordinatesUtil.z));
			world.getCamera().setPosition(sphericalCoordinatesUtil.x,sphericalCoordinatesUtil.y,sphericalCoordinatesUtil.z);
//			world.getCamera().setPosition(-80,-80,-80);
			world.getCamera().lookAt(new SimpleVector(0,0,0));



//			phone.rotateAxis(new SimpleVector(0,0,0),sphericalCoordinatesUtil.fai);
//			phone.rotateAxis(rotationPivot,sphericalCoordinatesUtil.fai);

//			phone.r
//			phone.r
//			sphericalCoordinatesUtil.toZero();

//			world.getCamera().rotateCameraY(CC);
//			world.getCamera().

//			world.getCamera().lookAt(new SimpleVector(0,0,0));

			SimpleVector position = world.getCamera().getPosition();

			runOnUiThread(
					new Runnable() {
						@Override
						public void run() {
							tv_camera.setText(String.format("x: %.3f \n y: %.3f \n z: %.3f ",position.x,position.y,position.z));
							tv_camera.append(String.format("ceta: %.3f \n fai: %.3f ",sphericalCoordinatesUtil.ceta, sphericalCoordinatesUtil.fai));
						}
					}
			);

//			CCC.translate(AA,BB,CC);
			CCC.setOrigin(new SimpleVector(sphericalCoordinatesUtil.x/2,sphericalCoordinatesUtil.y/2,sphericalCoordinatesUtil.z/2));


//			Log.e("hanshunli","camera->>>"+world.getCamera().getPosition());

			// 用给定的颜色(back)清除FrameBuffer

			fb.clear(back);
			// // 变换和灯光所有多边形
			world.renderScene(fb);
			// 绘制
			world.draw(fb);
			// 渲染图像显示
			fb.display();

			// 记录FPS
			if (System.currentTimeMillis() - time >= 1000) {
				Logger.log(fps + "fps");
				fps = 0;
				time = System.currentTimeMillis();
			}
			fps++;
			progressDialog.dismiss();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		phone.clearObject();
		master = null;
		TextureManager.getInstance().removeTexture("samSungTexture");
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

//		System.out.println("onDestrory!!!!!!!!!!!!!!!!!!!!!!!");
//		
//		cube.clearObject();
//		fb.freeMemory();
		//world.removeAllObjects();
	}


	public class SimpleOnScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
		float beginRadius = 0;
		float lastScale =1;
		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			Log.e("hanshunli","detector "+detector.getScaleFactor());
			float scaleFactor = detector.getScaleFactor();

			float v = scaleFactor - lastScale;


			sphericalCoordinatesUtil.r-=v*100;
//			phone.scale(1+v);

			lastScale = scaleFactor;

			return false;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			lastScale=1;

			touchTurn = 0;
			touchTurnUp = 0;
		}
	}

}
