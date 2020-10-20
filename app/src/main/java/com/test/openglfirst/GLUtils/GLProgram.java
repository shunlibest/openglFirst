package com.test.openglfirst.GLUtils;

import android.opengl.GLES20;

public class GLProgram {


	private int mProgram;

	/**
	 * 第一步, 创建一个gl程序
	 * @param vertexShader 顶点着色器的引用
	 * @param fragmentShader 片元着色器的引用
	 */
	public void glCreate(int vertexShader, int fragmentShader){
		//创建一个空的OpenGLES程序
		mProgram = GLES20.glCreateProgram();
		//将顶点着色器加入到程序
		GLES20.glAttachShader(mProgram, vertexShader);
		//将片元着色器加入到程序中
		GLES20.glAttachShader(mProgram, fragmentShader);
		//连接到着色器程序
		GLES20.glLinkProgram(mProgram);
	}

	//将程序加入到OpenGLES2.0环境
	public void startUse(){
		GLES20.glUseProgram(mProgram);
	}

	public int getAttribute(String attribute){

		//获取顶点着色器的vPosition成员句柄
		int attribLocation = GLES20.glGetAttribLocation(mProgram, attribute);
		//启用三角形顶点的句柄
		GLES20.glEnableVertexAttribArray(attribLocation);

		return attribLocation;
	}

	public int getUniform(String uniform){
		//获取顶点着色器的vPosition成员句柄
		int uniformLocation  = GLES20.glGetUniformLocation(mProgram, uniform);
		return uniformLocation;
	}




}
