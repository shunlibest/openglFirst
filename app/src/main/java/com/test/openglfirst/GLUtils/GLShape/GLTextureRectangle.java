/*
 *
 * SGLRender.java
 * 
 * Created by Wuwang on 2016/10/15
 */
package com.test.openglfirst.GLUtils.GLShape;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import com.test.openglfirst.GLUtils.GLProgram;
import com.test.openglfirst.GLUtils.GLUtil;
import com.test.openglfirst.GLUtils.ShaderUtil;
import com.test.openglfirst.GLUtils.base.BaseSurfaceViewRender;
import com.test.openglfirst.GLUtils.base.GLError;
import com.test.openglfirst.MyApplication;
import com.test.openglfirst.R;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Description:
 */
public class GLTextureRectangle extends BaseSurfaceViewRender {


    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f }; //白色

    static float rectangleCoords[] = {
            -1f,  1f, 0f, // top left
            -1f, -1f, 0.0f, // bottom left
            1f, 1f, 0.0f, // bottom right
            1f,  -1f, 0.0f  // top right
    };
//
//    private final float[] sCoord={
//            0.0f,0.0f,
//            0.0f,1.0f,
//            1.0f,0.0f,
//            1.0f,1.0f,
//    };
    private final float[] sCoord={
            0.0f,0.0f, 0f,
            0.0f,1.0f, 0f,
            1.0f,0.0f, 0f,
            1.0f,1.0f, 0f
    };

    @Override
    protected String loadVertexShader() {
        return "shaders/texture_rectangle.vert";
    }

    @Override
    protected String loadFragmentShader() {
        return "shaders/texture_rectangle.frag";
    }

    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    @Override
    protected void onSurfaceCreated() {
        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

        float ratio=(float)width/height;
        //设置透视投影
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0.4f, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

    }

    @Override
    protected void initValues() {
//        Bitmap bitmap =BitmapFactory.decodeResource(MyApplication.getContext().getResources() , R.drawable.fengj);
////
//
//        texture = GLUtil.createTexture(bitmap);

        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

        textureIdMoon = initTexture(R.mipmap.moon);
//
    }
    int textureIdMoon;
    int texture;
    @Override
    protected void onDrawFrame() {

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//


        Log.e("hanshunli","texture"+glProgram.toString());
        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

        int vPosition = getAttribute("vPosition");
        GLUtil.setAttribPointer(vPosition,rectangleCoords);
        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

        int vCoordinate = getAttribute("vCoordinate");
        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

        GLUtil.setAttribPointer(vCoordinate,sCoord);
        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

        int vMatrix = getUniform("vMatrix");
        GLUtil.setUniformMat4(vMatrix,mMVPMatrix);

        //获取片元着色器的vColor成员的句柄
//        int aColor = glProgram.getAttribute("aColor");
        //设置绘制三角形的颜色


        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");


        int vTexture = getUniform("vTexturex");


        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");


//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+3);


        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, vTexture);


        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");
//        GLES20.glUniform1i(vTexture, 3);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIdMoon);


        GLES20.glUniform1i(vTexture, 0);
        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");


        //绘制矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, rectangleCoords.length/3);



        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(vPosition);
//        GLES20.glDisableVertexAttribArray(vCoordinate);

        Log.e("hanshunli","6666666");
        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

    }


    public int initTexture(int drawableId)//textureId
    {
        //生成纹理ID
        int[] textures = new int[1];
        GLES20.glGenTextures
                (
                        1,          //产生的纹理id的数量
                        textures,   //纹理id的数组
                        0           //偏移量
                );
        int textureId=textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);

        //通过输入流加载图片===============begin===================
        InputStream is = MyApplication.getContext().getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try
        {
            bitmapTmp = BitmapFactory.decodeStream(is);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        //通过输入流加载图片===============end=====================

        //实际加载纹理
        GLUtils.texImage2D
                (
                        GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
                        0,                      //纹理的层次，0表示基本图像层，可以理解为直接贴图
                        bitmapTmp,              //纹理图像
                        0                      //纹理边框尺寸
                );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片

        return textureId;
    }
}
