package com.test.openglfirst.GLUtils.GLShape;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.test.openglfirst.GLUtils.GLUtil;
import com.test.openglfirst.GLUtils.base.BaseSurfaceViewRender;

public class GLTriangle extends BaseSurfaceViewRender {

	@Override
	protected String loadVertexShader() {
		return "shaders/base.vert";
	}

	@Override
	protected String loadFragmentShader() {
		return "shaders/base.frag";
	}


	@Override
	protected void onSurfaceCreated() {

	}

	private float[] mViewMatrix=new float[16];
	private float[] mProjectMatrix=new float[16];
	private float[] mMVPMatrix=new float[16];



	@Override
	protected void onSurfaceChanged(int width, int height) {
		//计算宽高比
		float ratio=(float)width/height;
		//设置透视投影
		Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

	}

	@Override
	protected void initValues() {

	}

	@Override
	protected void onDrawFrame() {

		//设置相机位置
//		Matrix.setLookAtM(cameraMatrix, 0,
//				5.0f, 5.0f, 10.0f,
//				v1, v2, v3,
//				0f, 0f, 1f);

		//设置相机位置
		Matrix.setLookAtM(mViewMatrix, 0,
				0, 0, 7.0f,
				0f, 0f, 0f,
				0f, 1.0f, 0.0f);
		//计算变换矩阵

		Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

		int vMatrix = getUniform("vMatrix");
		GLUtil.setUniformMat4(vMatrix,mMVPMatrix);


		int vPosition = getAttribute("vPosition");
		float[] triangleCoords = {
				v1,  v2, 0, // top
				-1f, -0.5f, 0.0f, // bottom left
				0.5f, -1.5f, 0.0f  // bottom right
		};

		GLUtil.setAttribPointer(vPosition,triangleCoords);


		//获取片元着色器的vColor成员的句柄
		int aColor = getAttribute("aColor");
		//设置绘制三角形的颜色

		//设置颜色
		float[] color = {
				1f, 1.0f, 1, 1.0f ,
				1, 1, 1, 1.0f,
				1, 1, 1.0f, 1.0f
		};


		GLUtil.setAttribColor(aColor,color);

//		int vColor = GLProgram.getInstance().getUniform("vColor");//获取片元着色器的vColor成员的句柄
//		GLES20.glUniform4fv(vColor, 1, color, 0);//设置绘制三角形的颜色

		//绘制三角形
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0,triangleCoords.length/3);
		//禁止顶点数组的句柄
		GLES20.glDisableVertexAttribArray(vPosition);




	}
	private float[] cameraMatrix=new float[16];

	float v1,v2,v3;
	public void updatePoints(float v1, float v2, float v3) {
		this.v1=v1;
		this.v2=v2;
		this.v3=v3;
	}
}
