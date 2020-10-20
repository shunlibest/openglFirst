package com.test.openglfirst.GLUtils.GLShape;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.test.openglfirst.GLUtils.GLUtil;
import com.test.openglfirst.GLUtils.base.BaseSurfaceViewRender;
import com.test.openglfirst.GLUtils.base.GLError;
import com.test.openglfirst.SphericalCoordinatesUtil;

public class GLCube extends BaseSurfaceViewRender {
//	public float yrotate=0;
//	public float xrotate =0;
//	public float XScalef =0;
//	public float YScalef =0 ;
//	public float ZScalef=0;
//	public float radius =1;
	public SphericalCoordinatesUtil spherical = new SphericalCoordinatesUtil();

//	GLSurfaceView gl_view;

//	final float cubePositions[] = {
//			0, 0, 0,
//			0, 0, 1,
//			0, 1, 1,
//			0, 1, 0,
//			1, 0, 0,
//			1, 0, 1,
//			1, 1, 1,
//			1, 1, 0,
//	};

//	float[] cubePositions[] = vert;
//
//	final short index[]={
//			0, 1, 3,
//			1, 2, 3,
//			0, 7, 4,
//			0, 3, 7,
//			5, 4, 6,
//			6, 4, 7,
//			2, 6, 3,
//			6, 7, 3,
//			1, 5, 2,
//			2, 5, 6,
//			1, 0, 5,
//			5, 0, 4,
//	};

//
	final float cubePositions[] = {
			-1.0f,1.0f,1.0f,    //正面左上0
			-1.0f,-1.0f,1.0f,   //正面左下1
			1.0f,-1.0f,1.0f,    //正面右下2
			1.0f,1.0f,1.0f,     //正面右上3
			-1.0f,1.0f,-1.0f,    //反面左上4
			-1.0f,-1.0f,-1.0f,   //反面左下5
			1.0f,-1.0f,-1.0f,    //反面右下6
			1.0f,1.0f,-1.0f,     //反面右上7
	};
//
//
	final short index[]={
			0,3,2,0,2,1,    //正面
			0,1,5,0,5,4,    //左面
			0,7,3,0,4,7,    //上面
			6,7,4,6,4,5,    //后面
			6,3,7,6,2,3,    //右面
			6,5,1,6,1,2     //下面
	};




	@Override
	protected String loadVertexShader() {
		return "shaders/cube.vert";
	}

	@Override
	protected String loadFragmentShader() {
		return "shaders/cube.frag";
	}



	private float[] mViewMatrix=new float[16];
	private float[] mProjectMatrix=new float[16];
	private float[] mMVPMatrix=new float[16];

	private float[] mCenterMatrix=new float[16];
	private float[] mModelMatrix=new float[16];

	@Override
	protected void onSurfaceCreated() {

	}

	private final float[] mBaseMatrix = new float[]{
			1f, 0f, 0f, 0f,
			0f, 1f, 0f, 0f,
			0f, 0f, 1f, 0f,
			0f, 0f, 0f, 1f
	};


	@Override
	protected void onSurfaceChanged(int width, int height) {
		//计算宽高比
		float ratio=(float)width/height;
		//设置透视投影
		Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 1, 1000);

		//x y z 代表位移量
		Matrix.translateM(mModelMatrix,0,mBaseMatrix,0,-cX,-cY,-cZ );

		for (int i = 0; i < mModelMatrix.length; i++) {
			Log.e("han"," "+i+" "+mModelMatrix[i++] +" " +mModelMatrix[i++]+" "+mModelMatrix[i++]+" "+mModelMatrix[i] );

		}
		mCenterMatrix = new float[]{
				1,0,0,0,
				0,1,0,0,
				0,0,1,0,
				0,-0,-0 ,1
		};

//		Matrix.multiplyMM(mProjectMatrix,0,mCenterMatrix,0,mProjectMatrix,0);
//

	}

	@Override
	protected void initValues() {

	}

	float lookX = 0, lookY=0,lookZ=0;

	@Override
	protected void onDrawFrame() {
		GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");


//		//
//		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
//		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//

		GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

		spherical.toXYZ();

		//设置相机位置
		Matrix.setLookAtM(mViewMatrix, 0,
				spherical.x +0,spherical.y +0,spherical.z +0,
				0 + 0,0 + 0,0+0,
				0f, 1.0f, 0.0f);

//		Matrix.setLookAtM(mViewMatrix, 0,
//				3,3, 7.0f+SaveHelper.qz11,
//				0,0,0,
//
//				0f, 1.0f, 0.0f);

//		Matrix.multiplyMM(mMVPMatrix,0,mViewMatrix,0,mModelMatrix,0);

		//计算变换矩阵
		Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);






//		Matrix.multiplyMM(mMVPMatrix,0,mMVPMatrix,0,mCenterMatrix,0);

		int vPosition = getAttribute("vPosition");
		GLUtil.setAttribPointer(vPosition,cubePositions);


		int vMatrix = getUniform("vMatrix");
		GLUtil.setUniformMat4(vMatrix,mMVPMatrix);


//		//获取片元着色器的vColor成员的句柄
		int aColor = getAttribute("aColor");
////		//设置绘制三角形的颜色
////
////		//设置颜色
////
		float[] color = {
				1f,1f,0f,1f,
				1f,1f,0f,1f,
				1f,1f,0f,1f,
				1f,1f,0f,1f,
				1f,0f,0f,1f,
				1f,0f,0f,1f,
				1f,0f,0f,1f,
				1f,0f,0f,1f
		};
		GLUtil.setAttribColor(aColor,color);


		int vColor = getUniform("vColor");

		//设置绘制三角形的颜色
//		GLES20.glUniform4fv(vColor, 1, color, 0);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length, GLES20.GL_UNSIGNED_SHORT,GLUtil.index2Buffer(index));

		//禁止顶点数组的句柄
		GLES20.glDisableVertexAttribArray(vPosition);

		GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

	}


	float v1,v2,v3;
	public void updatePoints(float v1, float v2, float v3) {
		this.v1=v1;
		this.v2=v2;
		this.v3=v3;
	}

	float x=0,y=0,z=0;
	public void updateXY(float x, float y) {
		this.x=x;
		this.y=y;

	}

	float XCross,YCross;

	float lastXCross,lastYCross;

	int k=6;
	public void updateXYCross(float XCross, float YCross) {
		this.XCross=XCross;
		this.YCross=YCross;

//		XCross=XCross-lastXCross;
//		YCross=YCross-lastYCross;
//
//		lastXCross=XCross;
//		lastYCross=YCross;
//		if (XCross==0 && YCross==0){
//			return;
//		}

		lookX=(float) Math.sin(Math.toRadians(YCross))*(float) Math.cos(Math.toRadians(XCross))*k;
		lookY=(float) Math.sin(Math.toRadians(YCross))+(float) Math.sin(Math.toRadians(XCross))*k;
		lookZ=(float) Math.cos(Math.toRadians(YCross))*k;

	}

//	float[] vert;
//	short[] vert1;
//	public void setValues(float[] vert, short[] vert1) {
//		this.vert=vert;
//		this.vert1=vert1;
//
//	}
	float cX=0,cY=0,cZ=0;

	public void setCenter(double cX, double cY, double cZ) {
		this.cX=(float) cX;
		this.cY=(float) cY;
		this.cZ=(float) cZ;

		Log.e("hanshunli","CX:"+cX+"CY:"+cY+"CZ:"+cZ);
	}
}
