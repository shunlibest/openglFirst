package com.test.openglfirst.GLUtils.base;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.test.openglfirst.GLUtils.GLProgram;
import com.test.openglfirst.GLUtils.ShaderUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class  BaseSurfaceViewRender implements GLSurfaceView.Renderer {

	protected GLProgram glProgram = new GLProgram();


	protected abstract String loadVertexShader();
	protected abstract String loadFragmentShader();

	@Override
	final public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

		Log.e("render","onSurfaceCreated");

		//将背景设置为灰色
		GLES20.glClearColor(0.5f,0.5f,0.5f,1.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//		GLES20.glClearColor(32.0f / 255.0f, 35.0f / 255.0f, 46.0f / 255.0f, 1);
//		GLES20.glEnable(GLES20.GL_BLEND);
//		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

		int vertexShader = ShaderUtil.loadVertexShader(loadVertexShader());
		int fragmentShader =  ShaderUtil.loadFragmentShader(loadFragmentShader());
		glProgram.glCreate(vertexShader,fragmentShader);



		onSurfaceCreated();
		GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

		initValues();
	}

	@Override
	final public void onSurfaceChanged(GL10 gl10, int i, int i1) {
		GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

//		GLConfig.setViewPort(i,i1);
		onSurfaceChanged(i,i1);

		GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

	}

	@Override
	final public void onDrawFrame(GL10 gl10) {
		GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");
//		Log.e("hanshunli","33333");

		glProgram.startUse();
//		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
//		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//		GLES20.glDepthMask(true);

//


		onDrawFrame();
	}

	protected abstract void onSurfaceCreated();
	protected abstract void onSurfaceChanged(int width,int height);
	protected abstract void initValues();

	protected abstract void onDrawFrame();



	protected int getAttribute(String attribute){
		int attr = glProgram.getAttribute(attribute);
		return attr;
	}

	protected int getUniform(String uniform){
		int uni = glProgram.getUniform(uniform);
		return uni;
	}


	protected void multiply(float[] resMatrix ,float[] aMatrix,float[] bMatrix){
		Matrix.multiplyMM(resMatrix, 0, aMatrix, 0, bMatrix, 0);
	}



}
