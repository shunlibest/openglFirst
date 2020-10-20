package com.test.openglfirst;

import android.app.Activity;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.threed.jpct.Camera;
import com.threed.jpct.Config;
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

public class JpctExampleActivity extends Activity {
	//JpctAdvancedExampleActivity对象用来处理activity的onPause和onResume
	public static JpctExampleActivity master=null;

	private GLSurfaceView mGLView;
	private MRenderer mRenderer=null;

	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;

	private int move=0;
	private float turn=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// 如果本类对象不为NULL,将从Object中所有属性装入该类
		if(master!=null){
			copy(master);
		}

		super.onCreate(savedInstanceState);
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
//		renderer = new HelloWorld.MyRenderer();
//		mGLView.setRenderer(renderer);
		mRenderer=new MRenderer();
		mGLView.setRenderer(mRenderer);
		setContentView(mGLView);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mGLView.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


	//实现手势触摸移动
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//按下手指
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			xpos=event.getX();
			ypos=event.getY();
			return true;
		}
		//抬起手指
		if (event.getAction() == MotionEvent.ACTION_UP) {
			xpos = -1;
			ypos = -1;
			touchTurn = 0;
			touchTurnUp = 0;
			return true;
		}
		//移动
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			float xd = event.getX() - xpos;
			float yd = event.getY() - ypos;

			xpos = event.getX();
			ypos = event.getY();

			touchTurn = xd / 100f;
			touchTurnUp = yd / 100f;
			return true;
		}

		try {
			Thread.sleep(15);
		} catch (Exception e) {

		}

		return super.onTouchEvent(event);
	}

	//实现上下左右键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
			move=2;
			return  true;
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			move = -2;
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			turn = 0.05f;
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			turn = -0.05f;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			move = 0;
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			move = 0;
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			turn = 0;
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			turn = 0;
			return true;
		}
		return super.onKeyUp(keyCode, event);
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


	class MRenderer implements GLSurfaceView.Renderer {

		private FrameBuffer fb=null;
		private World world=null;
		private RGBColor back=new RGBColor(100,100,100);
		private Object3D plane=null;//平面
		private Object3D tree1=null;//树
		private Object3D tree2=null;
		private Object3D rock=null;//石头
		private Object3D grass=null;//草
//		private Object3D cube=null;//长方体
		private Object3D house=null;//房子
		private Object3D head=null;//一个骷髅头
		private Light light=null;

		private Object3D cubeOBJ=null;//一个骷髅头


		private long time=System.currentTimeMillis();//返回的是以毫秒为单位的当前时间
		private SimpleVector lightRot=new SimpleVector(-100,-100f,100);

		public MRenderer(){
			//此代码在下一个例子中会解释
			Config.maxPolysVisible = 500;
			Config.farPlane = 1500;
			Config.glTransparencyMul = 0.1f;
			Config.glTransparencyOffset = 0.1f;
			Texture.defaultToMipmapping(true);
			Texture.defaultTo4bpp(true);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			// TODO Auto-generated method stub

			try {
				if (true) {
					Camera cam = world.getCamera();
					if (turn != 0) {
						world.getCamera().rotateY(-turn);
					}

					if (touchTurn != 0) {
						world.getCamera().rotateY(touchTurn);
						touchTurn = 0;
					}

					if (touchTurnUp != 0) {
						world.getCamera().rotateX(touchTurnUp);
						touchTurnUp = 0;
					}

					if (move != 0) {
						world.getCamera().moveCamera(cam.getDirection(), move);
					}

					fb.clear(back);
					world.renderScene(fb);//转换灯光和所有的多边形
					world.draw(fb);
					fb.display();//显示

					if (light != null) {
						light.rotate(lightRot, plane.getTransformedCenter());
					}
				} else {
					if (fb != null) {
						fb.dispose();
						fb = null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Logger.log("Drawing thread terminated!", Logger.MESSAGE);
			}
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// TODO Auto-generated method stub
			if(fb!=null) {
				fb.dispose();//清理缓冲器
			}
			fb=new FrameBuffer(gl,width,height);

		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// TODO Auto-generated method stub
			if(JpctExampleActivity.master==null){
				world=new World();
				Resources res = getResources();
				//取得材质管理器
				TextureManager tm=TextureManager.getInstance();
				//取得纹理
				Texture grass2 = new Texture(res.openRawResource(R.raw.grassy));
				Texture leaves = new Texture(res.openRawResource(R.raw.tree2y));
				Texture leaves2 = new Texture(res.openRawResource(R.raw.tree3y));
				Texture rocky = new Texture(res.openRawResource(R.raw.rocky));
				Texture planetex = new Texture(res.openRawResource(R.raw.planetex));
				Texture housey2 = new Texture(res.openRawResource(R.raw.photo));
				Texture heady=new Texture(res.openRawResource(R.raw.photo));
				//将纹理添加到材质管理器里面
				tm.addTexture("grass2", grass2);
				tm.addTexture("leaves", leaves);
				tm.addTexture("leaves2", leaves2);
				tm.addTexture("rock", rocky);
				tm.addTexture("grassy", planetex);
				tm.addTexture("housey2",housey2);
				tm.addTexture("heady",heady);
				//构造平面，该出用的是经过处理的数据
				plane = Loader.loadSerializedObject(res.openRawResource(R.raw.serplane));
				//加载岩石，树，草，房子，骷髅头的3ds模型
				//查看API文档可知，Loader.load3DS返回的是Object3D[]，是一个数组，这里选择数组的第一项
				//数组第一项就是我们需要的模型，所以写为Loader.load3DS(  )[0];
				rock=Loader.load3DS(res.openRawResource(R.raw.rock), 30f)[0];
				tree1=Loader.load3DS(res.openRawResource(R.raw.tree2), 5f)[0];
				tree2=Loader.load3DS(res.openRawResource(R.raw.tree3), 5f)[0];
				grass=Loader.load3DS(res.openRawResource(R.raw.grass), 2f)[0];
//				cube=Loader.load3DS(res.openRawResource(R.raw.cube), 1f)[0];
				//?????????? 在这里有些问题，house和head这两个3ds模型加载不进去？？？？？？？？？
				house=Loader.load3DS(res.openRawResource(R.raw.house1), 1f)[0];
				head=Loader.load3DS(res.openRawResource(R.raw.head), 1f)[0];
				//上面的house和head两个模型在最终的画面里面看不到，没有出现，笔者也不知道是为什么？？？？？？？？？？？？？？

				grass.translate(-45, -17, -50);//平移
				grass.rotateZ((float) Math.PI);
				rock.translate(0, 0, -90);
				//绕X轴旋转90度，注意：rotateX（）默认的是逆时针方向旋转
				rock.rotateX(-(float) Math.PI / 2);
				tree1.translate(-50, -100, -50);
				tree1.rotateZ((float) Math.PI);
				tree2.translate(60, -95, -10);
				tree2.rotateZ((float) Math.PI);
				plane.rotateX((float) Math.PI / 2f);
//				cube.translate(-80, 0, -110);
//				cube.rotateX((float)Math.PI/2f);
				head.translate(-45, -17, -50);
				head.rotateZ((float) Math.PI);
				house.translate(-70, 0, -110);
				house.rotateX((float)Math.PI/2f);
				//下面的可要可不要
				house.setName("house");
				plane.setName("plane");
				tree1.setName("tree1");
				tree2.setName("tree2");
				grass.setName("grass");
				rock.setName("rock");
				//设置纹理方式
				house.calcTextureWrapSpherical();
				head.calcTextureWrapSpherical();
//				cube.calcTextureWrapSpherical();
				//设置纹理
				house.setTexture("housey2");
				head.setTexture("heady");
//				cube.setTexture("housey2");
				tree1.setTexture("leaves");
				tree2.setTexture("leaves2");
				rock.setTexture("rock");
				grass.setTexture("grass2");

				cubeOBJ = Primitives.getPlane(50,2);
				cubeOBJ.setTexture("rock");
				cubeOBJ.build();
				world.addObject(cubeOBJ);

				//加入到world里面
				world.addObject(plane);
//				world.addObject(tree1);
//				world.addObject(tree2);
//				world.addObject(grass);
//				world.addObject(rock);
//				world.addObject(house);
				world.addObject(head);
//				world.addObject(cube);
				//释放内存
				plane.strip();
//				tree1.strip();
//				tree2.strip();
//				grass.strip();
//				rock.strip();
//				house.strip();
				head.strip();
//				cube.strip();

				world.setAmbientLight(250, 250, 250);//设置光环境光
				world.buildAllObjects();//Calls build() for every object in the world.

				light = new Light(world);//设置光照
				light.setIntensity(250, 250, 0);//设置光颜色

				Camera cam = world.getCamera();
				cam.moveCamera(Camera.CAMERA_MOVEOUT, 250);
				cam.moveCamera(Camera.CAMERA_MOVEUP, 100);
				//相机的在plane的中心
				cam.lookAt(plane.getTransformedCenter());

				SimpleVector sv = new SimpleVector();
				sv.set(plane.getTransformedCenter());
				Log.e("X:", plane.getTransformedCenter().toString());
				sv.y -= 500;
				sv.x -= 300;
				sv.z += 200;
				//设置光照的位置，此处的sv为plane的中心
				light.setPosition(sv);
				//释放内存
				MemoryHelper.compact();
				if (master == null) {
					Logger.log("Saving master Activity!");
					master = JpctExampleActivity.this;
				}
			}
		}
	}
}