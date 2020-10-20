package com.test.openglfirst;

import android.app.Activity;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

import com.test.openglfirst.GLUtils.GLUtil;
import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.GLSLShader;
import com.threed.jpct.ITextureEffect;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;
import com.threed.jpct.VertexAttributes;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;
import org.smurn.jply.util.NormalMode;
import org.smurn.jply.util.NormalizingPlyReader;
import org.smurn.jply.util.TesselationMode;
import org.smurn.jply.util.TextureMode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 
 * 
 * @author EgonOlsen
 * 
 */
public class HelloShader extends Activity implements OnScaleGestureListener {

	// Used to handle pause and resume...
	private static HelloShader master = null;

	private ScaleGestureDetector gestureDec = null;

	private GLSurfaceView mGLView;
	private MyRenderer renderer = null;
	private FrameBuffer fb = null;
	private World world = null;
	private RGBColor back = new RGBColor(50, 50, 100);

	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;

	private Texture font = null;

	private Object3D plane;
	private Light light;

	private GLSLShader shader = null;

	private float scale = 0.05f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Logger.log("onCreate");
		Logger.setLogLevel(Logger.LL_DEBUG);

		if (master != null) {
			copy(master);
		}

		super.onCreate(savedInstanceState);
		mGLView = new GLSurfaceView(getApplication());

		// Enable the OpenGL ES2.0 context
		mGLView.setEGLContextClientVersion(2);

		try {
			read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderer = new MyRenderer();
		mGLView.setRenderer(renderer);
		setContentView(mGLView);

		gestureDec = new ScaleGestureDetector(this.getApplicationContext(), this);
	}

	@Override
	protected void onPause() {
		Logger.log("onPause");
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		Logger.log("onResume");
		super.onResume();
		mGLView.onResume();
	}

	@Override
	protected void onStop() {
		Logger.log("onStop");
		super.onStop();
	}

	private void copy(Object src) {
		try {
			Logger.log("Copying data from master Activity!");
			Field[] fs = src.getClass().getDeclaredFields();
			for (Field f : fs) {
				f.setAccessible(true);
				f.set(this, f.get(src));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {

		gestureDec.onTouchEvent(me);

		if (me.getAction() == MotionEvent.ACTION_DOWN) {
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

	protected boolean isFullscreenOpaque() {
		return true;
	}

	class MyRenderer implements GLSurfaceView.Renderer {

		private boolean hasToCreateBuffer = false;
		private GL10 lastInstance = null;
		private int w = 0;
		private int h = 0;
		private int fps = 0;
		private int lfps = 0;

		private long time = System.currentTimeMillis();

		public MyRenderer() {
			Texture.defaultToMipmapping(true);
			Texture.defaultTo4bpp(true);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			Resources res = getResources();

			if (master == null) {
				Logger.log("Initializing buffer...");
				fb = new FrameBuffer(width, height);
				
				Logger.log("Initializing game...");
				world = new World();

				TextureManager tm = TextureManager.getInstance();

				Texture face = new Texture(res.openRawResource(R.raw.face));
				Texture normals = new Texture(res.openRawResource(R.raw.face_norm), true);
				Texture heighty = new Texture(res.openRawResource(R.raw.face_height2));

				plane = Primitives.getPlane(1, 100);

//				SimpleVector simpleVector0 = new SimpleVector(0,0,0);
//				SimpleVector simpleVector1 = new SimpleVector(1,1,1);
//				SimpleVector simpleVector2 = new SimpleVector(2,2,20);
//
//				for (int i = 0; i < vert1.length; i++) {
//					SimpleVector s1 = new SimpleVector(vert[i++],vert[i++],vert[i++]);
//					SimpleVector s2 = new SimpleVector(vert[i++],vert[i++],vert[i++]);
//					SimpleVector s3 = new SimpleVector(vert[i++],vert[i++],vert[i++]);
//					plane.addTriangle(s1,s2,s3);
//				}


				VertexAttributes vertexAttributes = new VertexAttributes("position", vert, VertexAttributes.TYPE_THREE_FLOATS);
//
//				Object3D object3D = new Object3D(8171);
//
//				Mesh mesh = object3D.getMesh();
//				mesh.setLocked(false);
//				mesh.addVertexAttributes(vertexAttributes);


//				Primitives.

				TexelGrabber grabber = new TexelGrabber();
				heighty.setEffect(grabber);
				heighty.applyEffect();
				int[] heighties = grabber.getAlpha();

				AlphaMerger setter = new AlphaMerger(heighties);
				normals.setEffect(setter);
				normals.applyEffect();

				font = new Texture(res.openRawResource(R.raw.numbers));
				font.setMipmap(false);

				tm.addTexture("face", face);
				tm.addTexture("normals", normals);

				TextureInfo ti = new TextureInfo(TextureManager.getInstance().getTextureID("face"));
				ti.add(TextureManager.getInstance().getTextureID("normals"), TextureInfo.MODE_BLEND);

				plane.setTexture(ti);

				shader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.vertexshader_offset)), Loader.loadTextFile(res.openRawResource(R.raw.fragmentshader_offset)));
				int program = shader.getProgram();



				//获取顶点着色器的vPosition成员句柄
				int vPosition = GLES20.glGetAttribLocation(program, "vPosition");
				//启用三角形顶点的句柄
				GLES20.glEnableVertexAttribArray(vPosition);

				GLUtil.setAttribPointer(vPosition,vert);

				GLES20.glDrawElements(GLES20.GL_TRIANGLES,vert1.length, GLES20.GL_UNSIGNED_SHORT,GLUtil.index2Buffer(vert1));

				//禁止顶点数组的句柄
				GLES20.glDisableVertexAttribArray(vPosition);

//				plane.

				plane.setShader(shader);
				plane.setSpecularLighting(true);
				shader.setStaticUniform("invRadius", 0.0003f);


//				shader.

				plane.build();
				plane.strip();

//				object3D.build();
//				object3D.strip();

				world.addObject(plane);
//				world.addObject(object3D);

				light = new Light(world);
				light.enable();

				light.setIntensity(60, 50, 50);
				light.setPosition(SimpleVector.create(-10, -50, -100));

				world.setAmbientLight(10, 10, 10);

				Camera cam = world.getCamera();
				cam.moveCamera(Camera.CAMERA_MOVEOUT, 70);
				cam.lookAt(plane.getTransformedCenter());

				MemoryHelper.compact();

				world.compileAllObjects();

				if (master == null) {
					Logger.log("Saving master Activity!");
					master = HelloShader.this;
				}
			} else {
				if (lastInstance != gl) {
					Logger.log("Setting buffer creation flag...");
					this.hasToCreateBuffer = true;
					w = width;
					h = height;
				}
			}
			lastInstance = gl;
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			Logger.log("onSurfaceCreated");
		}

		@Override
		public void onDrawFrame(GL10 gl) {

			if (this.hasToCreateBuffer) {
				Logger.log("Recreating buffer...");
				hasToCreateBuffer = false;
				fb = new FrameBuffer(w, h);
			}

			if (touchTurn != 0) {
				plane.rotateY(touchTurn);
				touchTurn = 0;
			}

			if (touchTurnUp != 0) {
				plane.rotateX(touchTurnUp);
				touchTurnUp = 0;
			}

			shader.setUniform("heightScale", scale);

			fb.clear(back);
			world.renderScene(fb);
			world.draw(fb);
			blitNumber(lfps, 5, 5);
			fb.display();

			if (System.currentTimeMillis() - time >= 1000) {
				lfps = fps;
				fps = 0;
				time = System.currentTimeMillis();
			}
			fps++;
		}

		private void blitNumber(int number, int x, int y) {
			if (font != null) {
				String sNum = Integer.toString(number);

				for (int i = 0; i < sNum.length(); i++) {
					char cNum = sNum.charAt(i);
					int iNum = cNum - 48;
					fb.blit(font, iNum * 5, 0, x, y, 5, 9, 5, 9, 10, true, null);
					x += 5;
				}
			}
		}
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float div = detector.getCurrentSpan() - detector.getPreviousSpan();
		div /= 5000;

		scale += div;

		if (scale > 0.063f) {
			scale = 0.063f;
		}
		if (scale < 0) {
			scale = 0;
		}

		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
	}

	/**
	 * Merges the height map into the alpha channel of the normal map.
	 * 
	 * @author EgonOlsen
	 * 
	 */
	private static class AlphaMerger implements ITextureEffect {

		private int[] alpha = null;

		public AlphaMerger(int[] alpha) {
			this.alpha = alpha;
		}

		@Override
		public void apply(int[] arg0, int[] arg1) {
			int end = arg1.length;
			for (int i = 0; i < end; i++) {
				arg0[i] = arg1[i] & 0x00ffffff | alpha[i];
			}
		}

		@Override
		public boolean containsAlpha() {
			return true;
		}

		@Override
		public void init(Texture arg0) {
			// TODO Auto-generated method stub
		}
	}

	/**
	 * Extracts the alpha channel from a texture.
	 * 
	 * @author EgonOlsen
	 * 
	 */
	private static class TexelGrabber implements ITextureEffect {

		private int[] alpha = null;

		@Override
		public void apply(int[] arg0, int[] arg1) {
			alpha = new int[arg1.length];
			int end = arg1.length;
			for (int i = 0; i < end; i++) {
				alpha[i] = (arg1[i] << 24);
			}
		}

		public int[] getAlpha() {
			return alpha;
		}

		@Override
		public boolean containsAlpha() {
			return true;
		}

		@Override
		public void init(Texture arg0) {
			// TODO Auto-generated method stub
		}
	}
	float[] vert;
	short[] vert1;

	void read() throws IOException {
		// Open the PLY file

		InputStream is = this.getResources().openRawResource(R.raw.bunny);
		PlyReader plyReader = new PlyReaderFile(is);

		// Normalize the data in the PLY file to ensure that we only get
		// triangles and that the vertices have normal vectors assigned.
		plyReader = new NormalizingPlyReader(plyReader,
				TesselationMode.TRIANGLES,
				NormalMode.ADD_NORMALS_CCW,
				TextureMode.PASS_THROUGH);


		List<ElementType> elementTypes = plyReader.getElementTypes();
		Log.e("han",""+elementTypes.size());

//		for (int i = 0; i < elementTypes.size(); i++) {
//			Log.e("han1",""+elementTypes.get(i).getName());
//		}

		ElementReader reader = plyReader.nextElementReader();

		int count = reader.getCount();
		Log.e("han2","count---"+count);

		Element triangle = reader.readElement();

		vert = new float[count*3];

		int i=0;
		while (triangle != null) {

//			int[] indices = triangle.getIntList("vertex_index");
//			for (int index : indices) {
//				Log.e("han","index---"+index);
//			}
//			Log.e("han2","triangle---"+triangle.toString());




			double x = triangle.getDouble("x")*100;
			vert[i++]= (float) x;
			double y = triangle.getDouble("y")*100;
			vert[i++]= (float) y;
			double z = triangle.getDouble("z")*100;
			vert[i++]= (float) z;

			triangle = reader.readElement();
		}


		ElementReader reader2 = plyReader.nextElementReader();
		Element triangle2 = reader2.readElement();

		int count1 = reader2.getCount();
		 vert1 = new short[count1*3];
		i=0;
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		while (triangle2 != null) {

//			int[] indices = triangle.getIntList("vertex_index");
//			for (int index : indices) {
//				Log.e("han","index---"+index);
//			}

			int[] vertex_indices = triangle2.getIntList("vertex_index");


			for (int j = 0; j <vertex_indices.length; j++) {
				vert1[i]=(short)vertex_indices[j];
				i++;
			}


//			Log.e("han3","triangle2---"+triangle2.toString());
			triangle2 = reader2.readElement();
		}
		Log.e("han3333","vert1---"+vert1.length);


//		glCube.setValues(vert,vert1);
	}



}
