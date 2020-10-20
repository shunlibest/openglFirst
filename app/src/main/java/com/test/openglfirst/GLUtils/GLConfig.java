package com.test.openglfirst.GLUtils;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class GLConfig {

	/**
	 * 设置gl的版本号, 一般使用2 或者3
	 * @param glSurfaceView 设置对象
	 * @param version 版本号, 限定为2或者3
	 */
	public static void setVersion(GLSurfaceView glSurfaceView, int version){
		glSurfaceView.setEGLContextClientVersion(version);
	}

	/**
	 * 设置渲染模式
	 * @param glSurfaceView 设置对象
	 * @param renderMode ALWAYS: 渲染器会不停地渲染场景，ONCE: 只有在创建和调用requestRender()时才会刷新
	 */
	public static void seMode(GLSurfaceView glSurfaceView, RenderMode renderMode){
		if (renderMode == RenderMode.ONCE){
			glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		}else {
			glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}
	}


	public static void setViewPort(int width,int height){
		GLES20.glViewport(0,0,width,height);
	}



	public static void clearCanvas(){
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
	}



}
