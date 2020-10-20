package com.test.openglfirst.GLUtils.GLShape;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.test.openglfirst.GLUtils.base.BaseSurfaceViewRender;
import com.test.openglfirst.GLUtils.base.GLError;
import com.test.openglfirst.MyApplication;
import com.test.openglfirst.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by hbin on 2016/8/24.
 * 表示月球的类，为普通纹理球，未采用多重纹理
 */
public class GLMoon extends BaseSurfaceViewRender {

    int muMVPMatrixHandle;//总变换矩阵引用id
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maCameraHandle; //摄像机位置属性引用id
    int maPositionHandle; //顶点位置属性引用id
    int maNormalHandle; //顶点法向量属性引用id
    int maTexCoorHandle; //顶点纹理坐标属性引用id
    int maSunLightLocationHandle;//光源位置属性引用id

    String mVertexShader;//顶点着色器
    String mFragmentShader;//片元着色器

    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;

    public GLMoon()
    {
        //初始化顶点坐标与着色数据
        initVertexData(0.5f);
    }

    //初始化顶点坐标与纹理数据的方法
    public void initVertexData(float r){
        final float UNIT_SIZE=0.5f;
        ArrayList<Float> alVertix=new ArrayList<Float>();//存放顶点坐标的ArrayList
        final float angleSpan=10f;//将球进行单位切分的角度
        for(float vAngle=90;vAngle>-90;vAngle=vAngle-angleSpan)//垂直方向angleSpan度一份
        {
            for(float hAngle=360;hAngle>0;hAngle=hAngle-angleSpan)//水平方向angleSpan度一份
            {//纵向横向各到一个角度后计算对应的此点在球面上的坐标
                double xozLength=r*UNIT_SIZE* Math.cos(Math.toRadians(vAngle));
                float x1=(float)(xozLength* Math.cos(Math.toRadians(hAngle)));
                float z1=(float)(xozLength* Math.sin(Math.toRadians(hAngle)));
                float y1=(float)(r*UNIT_SIZE* Math.sin(Math.toRadians(vAngle)));

                xozLength=r*UNIT_SIZE* Math.cos(Math.toRadians(vAngle-angleSpan));
                float x2=(float)(xozLength* Math.cos(Math.toRadians(hAngle)));
                float z2=(float)(xozLength* Math.sin(Math.toRadians(hAngle)));
                float y2=(float)(r*UNIT_SIZE* Math.sin(Math.toRadians(vAngle-angleSpan)));

                xozLength=r*UNIT_SIZE* Math.cos(Math.toRadians(vAngle-angleSpan));
                float x3=(float)(xozLength* Math.cos(Math.toRadians(hAngle-angleSpan)));
                float z3=(float)(xozLength* Math.sin(Math.toRadians(hAngle-angleSpan)));
                float y3=(float)(r*UNIT_SIZE* Math.sin(Math.toRadians(vAngle-angleSpan)));

                xozLength=r*UNIT_SIZE* Math.cos(Math.toRadians(vAngle));
                float x4=(float)(xozLength* Math.cos(Math.toRadians(hAngle-angleSpan)));
                float z4=(float)(xozLength* Math.sin(Math.toRadians(hAngle-angleSpan)));
                float y4=(float)(r*UNIT_SIZE* Math.sin(Math.toRadians(vAngle)));

                //构建第一三角形
                alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
                alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
                alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
                //构建第二三角形
                alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
                alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
                alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
            }
        }
        vCount=alVertix.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标

        //将alVertix中的坐标值转存到一个float数组中
        float vertices[]=new float[vCount*3];
        for(int i=0;i<alVertix.size();i++)
        {
            vertices[i]=alVertix.get(i);
        }

        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题

        //将alTexCoor中的纹理坐标值转存到一个float数组中
        float[] texCoor=generateTexCoor//获取切分整图的纹理数组
                (
                        (int)(360/angleSpan), //纹理图切分的列数
                        (int)(180/angleSpan)  //纹理图切分的行数
                );
        ByteBuffer llbb = ByteBuffer.allocateDirect(texCoor.length*4);
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer=llbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);
    }





    //自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
        float[] result=new float[bw*bh*6*2];
        float sizew=1.0f/bw;//列数
        float sizeh=1.0f/bh;//行数
        int c=0;
        for(int i=0;i<bh;i++)
        {
            for(int j=0;j<bw;j++)
            {
                //每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
                float s=j*sizew;
                float t=i*sizeh;

                result[c++]=s;
                result[c++]=t;

                result[c++]=s;
                result[c++]=t+sizeh;

                result[c++]=s+sizew;
                result[c++]=t;

                result[c++]=s+sizew;
                result[c++]=t;

                result[c++]=s;
                result[c++]=t+sizeh;

                result[c++]=s+sizew;
                result[c++]=t+sizeh;
            }
        }
        return result;
    }

    @Override
    protected String loadVertexShader() {
        return "shaders/moon.vert";
    }

    @Override
    protected String loadFragmentShader() {
        return "shaders/moon.frag";
    }

    @Override
    protected void onSurfaceCreated() {
        //设置太阳灯光的初始位置
        MatrixState.setLightLocationSun(100,5,0);

        GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        //设置视窗大小及位置
        GLES20.glViewport(0, 0, width, height);
        //计算GLSurfaceView的宽高比
        float ratio= (float) width / height;
        //调用此方法计算产生透视投影矩阵
        MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
        //调用此方法产生摄像机9参数位置矩阵
        MatrixState.setCamera(0,0,7.2f,0f,0f,0f,0f,1.0f,0.0f);
        //打开背面剪裁
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        //初始化纹理
//        textureIdEarth=initTexture(R.mipmap.earth);
//        textureIdEarthNight=initTexture(R.mipmap.earthn);
//        textureIdMoon=initTexture(R.mipmap.moon);
        //设置太阳灯光的初始位置
        MatrixState.setLightLocationSun(100,5,0);

        //启动一个线程定时旋转地球、月球
//        new Thread()
//        {
//            float eAngle=0;//地球自转角度
//            float cAngle=0;//天球自转的角度
//
//            @Override
//            public void run()
//            {
//                while(true)
//                {
//                    //地球自转角度
//                    eAngle = (eAngle + 2) % 360;
//                    //天球自转角度
//                    cAngle=(cAngle+0.2f)%360;
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
    }

//    int texId;
    @Override
    protected void initValues() {
        maPositionHandle = getAttribute("aPosition");
        //获取程序中顶点经纬度属性引用id
        maTexCoorHandle= getAttribute( "aTexCoor");
        //获取程序中顶点法向量属性引用id
        maNormalHandle= getAttribute("aNormal");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = getUniform( "uMVPMatrix");
        //获取程序中摄像机位置引用id
        maCameraHandle= getUniform("uCamera");
        //获取程序中光源位置引用id
        maSunLightLocationHandle= getUniform("uLightLocationSun");
        //获取位置、旋转变换矩阵引用id
        muMMatrixHandle = getUniform( "uMMatrix");


//        texId=initTexture(R.mipmap.moon);
    }

    @Override
    protected void onDrawFrame() {


        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将摄像机位置传入shader程序
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        //将光源位置传入shader程序
        GLES20.glUniform3fv(maSunLightLocationHandle, 1, MatrixState.lightPositionFBSun);

        //为画笔指定顶点位置数据
        GLES20.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3*4,
                        mVertexBuffer
                );
        //为画笔指定顶点经纬度数据
        GLES20.glVertexAttribPointer
                (
                        maTexCoorHandle,
                        2,
                        GLES20.GL_FLOAT,
                        false,
                        2*4,
                        mTexCoorBuffer
                );
        //为画笔指定顶点法向量数据
        GLES20.glVertexAttribPointer
                (
                        maNormalHandle,
                        4,
                        GLES20.GL_FLOAT,
                        false,
                        3*4,
                        mVertexBuffer
                );
        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        //绑定纹理
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
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
