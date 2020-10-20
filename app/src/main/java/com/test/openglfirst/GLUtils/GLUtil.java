package com.test.openglfirst.GLUtils;

import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class GLUtil {



	public static void setAttribPointer(int mPositionHandle, float[] point3List){
		ByteBuffer bb = ByteBuffer.allocateDirect(point3List.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(point3List);
		vertexBuffer.position(0);
		//准备三角形的坐标数据
		GLES20.glVertexAttribPointer(mPositionHandle, 3,
				GLES20.GL_FLOAT, false,
				//顶点之间的偏移量
				3*4,
				vertexBuffer);
	}


	public static void setUniformMat4(int mMatrixHandler, float[] floats16){
		GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,floats16,0);
	}

	public static void setUniform1f(int mMatrixHandler, float value){
		GLES20.glUniform1f(mMatrixHandler,value);
	}

	public static void setUniform4f(int mMatrixHandler, float r,float g, float b){
		GLES20.glUniform4f(mMatrixHandler, r,g,b, 1);
	}
//	public static void setUniform4f(int mMatrixHandler, float r,float g, float b){
//		GLES20.glUniform4f(mMatrixHandler, r,g,b, 1);
//	}


	public static void setAttribColor(int vColor, float[] colors){

		ByteBuffer dd = ByteBuffer.allocateDirect(colors.length * Float.BYTES);
		dd.order(ByteOrder.nativeOrder());
		FloatBuffer colorBuffer = dd.asFloatBuffer();
		colorBuffer.put(colors);
		colorBuffer.position(0);

		GLES20.glEnableVertexAttribArray(vColor);

		//设置绘制三角形的颜色
		GLES20.glVertexAttribPointer(vColor,3,
				GLES20.GL_FLOAT,false,
				0,colorBuffer);
	}


	public static ShortBuffer index2Buffer(short[] index){
		ByteBuffer cc= ByteBuffer.allocateDirect(index.length*2);
		cc.order(ByteOrder.nativeOrder());
		ShortBuffer indexBuffer = cc.asShortBuffer();
		indexBuffer.put(index);
		indexBuffer.position(0);

		return indexBuffer;
	}

	public static IntBuffer index2Buffer(int[] index){
		ByteBuffer cc= ByteBuffer.allocateDirect(index.length*2);
		cc.order(ByteOrder.nativeOrder());
		IntBuffer indexBuffer = cc.asIntBuffer();
		indexBuffer.put(index);
		indexBuffer.position(0);
		return indexBuffer;
	}

	public static int createTexture(Bitmap bitmap){
		int[] texture=new int[1];
		if(bitmap!=null&&!bitmap.isRecycled()){
			//生成纹理
			GLES20.glGenTextures(1,texture,0);
			//生成纹理
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
			//设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
			//设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
			//设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
			//设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
			//根据以上指定的参数，生成一个2D纹理
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			return texture[0];
		}
		return 0;
	}

	/**
	 * 加载外部纹理
	 * @return
	 */
	public static int loadCameraTexture() {
		int[] tex = new int[1];
		//生成一个纹理
		GLES20.glGenTextures(1, tex, 0);
		//将此纹理绑定到外部纹理上
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
		//设置纹理过滤参数
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		//解除纹理绑定
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
		return tex[0];
	}


}
