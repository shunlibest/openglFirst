package com.test.openglfirst.GLUtils.GLShape;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;

import com.test.openglfirst.GLUtils.GLUtil;
import com.test.openglfirst.GLUtils.base.BaseSurfaceViewRender;
import com.test.openglfirst.GLUtils.cameraUtils.CameraXHelper;

/**
 * @author shunlihan
 */
public class GLCameraX extends BaseSurfaceViewRender {



	private GLSurfaceView gl_view;





	static float rectangleCoords[] = {
			-1f, 1f, -0f, // top left
			-1f, -1f, 0.0f, // bottom left
			1f, 1f, 0.0f, // bottom right
			1f, -1f, 0.0f  // top right
	};
	//

	private final float[] sCoord = {
			0.0f, 0.0f, 0f,
			0.0f, 1.0f, 0f,
			1.0f, 0.0f, 0f,
			1.0f, 1.0f, 0f
	};

	private SurfaceTexture mCameraTexture;




	public GLCameraX(Context context){
		LifecycleOwner lifecycleOwner = (LifecycleOwner) context;

		CameraXHelper.init(lifecycleOwner, new Preview.OnPreviewOutputUpdateListener() {
			@Override
			public void onUpdated(Preview.PreviewOutput output) {

				Log.e("hanshunli123","eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
				mCameraTexture = output.getSurfaceTexture();
			}
		});
	}

	@Override
	protected String loadVertexShader() {
		return "shaders/camera.vert";
	}

	@Override
	protected String loadFragmentShader() {
		return "shaders/camera.frag";
	}


	int textureId;


	private float[] mViewMatrix = new float[16];
	private float[] mProjectMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];

	private float[] transformMatrix = new float[16];


	@Override
	protected void onSurfaceCreated() {

	}

	@Override
	protected void onSurfaceChanged(int width, int height) {
//		float ratio = (float) width / height;
		//设置透视投影
		//设置透视投影
//		Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
//		//设置相机位置
//		Matrix.setLookAtM(mViewMatrix, 0, 0, 0.4f, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//		//计算变换矩阵
//		Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
	}

	@Override
	protected void initValues() {

//		textureId = GLUtil.loadCameraTexture();
//		Log.e("hansuhnli", "textureId--" + textureId);


		mCameraTexture.attachToGLContext(textureId);

		mCameraTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
			@Override
			public void onFrameAvailable(SurfaceTexture surfaceTexture) {

			}
		});
	}

	@Override
	protected void onDrawFrame() {


		if (mCameraTexture != null) {
			//更新纹理图像
			mCameraTexture.updateTexImage();
			//获取外部纹理的矩阵，用来确定纹理的采样位置，没有此矩阵可能导致图像翻转等问题
//			mCameraTexture.getTransformMatrix(transformMatrix);
		}



		int vPosition = getAttribute("vPosition");
		GLUtil.setAttribPointer(vPosition, rectangleCoords);

		int vCoordinate = getAttribute("vCoordinate");

		GLUtil.setAttribPointer(vCoordinate, sCoord);

//		int vMatrix = getUniform("vMatrix");
//
//		GLUtil.setUniformMat4(vMatrix, mMVPMatrix);



		//获取片元着色器的vColor成员的句柄
//        int aColor = glProgram.getAttribute("aColor");
		//设置绘制三角形的颜色


		int vTexture = glProgram.getUniform("vTexture");
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
		GLES20.glUniform1i(vTexture, 0);

//		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
//		GLES20.glUniform1i(vTexture,0);


		//绘制矩形
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, rectangleCoords.length / 3);

		//禁止顶点数组的句柄
		GLES20.glDisableVertexAttribArray(vPosition);
		GLES20.glDisableVertexAttribArray(vCoordinate);
	}

//	private SurfaceTexture mSurfaceTexture;



	public void setSurfaceView(GLSurfaceView gl_view) {
		this.gl_view = gl_view;
	}

	public int getTextureId() {
		return textureId;
	}


}
