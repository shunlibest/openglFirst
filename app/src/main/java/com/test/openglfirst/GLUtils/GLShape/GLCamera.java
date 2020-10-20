package com.test.openglfirst.GLUtils.GLShape;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.test.openglfirst.GLUtils.GLUtil;
import com.test.openglfirst.GLUtils.base.BaseSurfaceViewRender;
import com.test.openglfirst.GLUtils.cameraUtils.KitkatCamera;

import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.setIdentityM;

/**
 * @author shunlihan
 */
public class GLCamera extends BaseSurfaceViewRender {



	private GLSurfaceView gl_view;


	static float rectangleCoords[] = {
			-1f, 1f, -1f, // top left
			-1f, -1f, -1f, // bottom left
			1f, 1f, -1f, // bottom right
			1f, -1f,-1f  // top right
	};
	//

	private final float[] sCoord = {
			0.0f, 0.0f, 0f,
			0.0f, 1.0f, 0f,
			1.0f, 0.0f, 0f,
			1.0f, 1.0f, 0f
	};

	int cameraId;
	KitkatCamera kitkatCamera;
	public GLCamera(GLSurfaceView gl_view,int cameraID,KitkatCamera kitkatCamera) {
		this.gl_view = gl_view;
		this.cameraId = cameraID;
		this.kitkatCamera= kitkatCamera;

	}

//	private SurfaceTexture mCameraTexture;



//	public void GLCamera(Context context){
//		LifecycleOwner lifecycleOwner = (LifecycleOwner) context;
//
//		CameraXHelper.init(lifecycleOwner, new Preview.OnPreviewOutputUpdateListener() {
//			@Override
//			public void onUpdated(Preview.PreviewOutput output) {
//				mCameraTexture = output.getSurfaceTexture();
//			}
//		});
//	}

	@Override
	protected String loadVertexShader() {
		return "shaders/camera.vert";
	}

	@Override
	protected String loadFragmentShader() {
		return "shaders/camera.frag";
	}





	private final float[] finalMatrix = new float[16];


	private final float[] projectionMatrix = new float[16];
	private final float[] viewMatrix = new float[16];
	private final float[] modelMatrix = new float[16];



	@Override
	protected void onSurfaceCreated() {

	}

	@Override
	protected void onSurfaceChanged(int width, int height) {
		//设置透视投影
		//设置透视投影
//
//		//设置相机位置
//		Matrix.setLookAtM(mViewMatrix, 0, 0, 0.4f, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//		//计算变换矩阵
//		Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

		glViewport(0, 0, width, height);
		float ratio = (float) width / height;

//		MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
//				/ (float) height, 0.1f, 100f);

		Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

		setIdentityM(finalMatrix, 0);
	}

	@Override
	protected void initValues() {
//		kitkatCamera.open(0);
//		textureId = GLUtil.loadCameraTexture();
		Log.e("hansuhnli", "textureId--" + "textureId");
		loadSurfaceTexture(cameraId);
	}

	@Override
	protected void onDrawFrame() {
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

		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraId);
		GLES20.glUniform1i(vTexture, 0);

//		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
//		GLES20.glUniform1i(vTexture,0);


		if (mSurfaceTexture != null) {
			//更新纹理图像
			mSurfaceTexture.updateTexImage();
			//获取外部纹理的矩阵，用来确定纹理的采样位置，没有此矩阵可能导致图像翻转等问题
			mSurfaceTexture.getTransformMatrix(projectionMatrix);
		}

		//绘制矩形
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, rectangleCoords.length / 3);

		//禁止顶点数组的句柄
		GLES20.glDisableVertexAttribArray(vPosition);
		GLES20.glDisableVertexAttribArray(vCoordinate);
	}

	private SurfaceTexture mSurfaceTexture;

	public boolean loadSurfaceTexture(int textureId) {
		//根据纹理ID创建SurfaceTexture
		mSurfaceTexture = new SurfaceTexture(textureId);
		mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
			@Override
			public void onFrameAvailable(SurfaceTexture surfaceTexture) {
				// 渲染帧数据

				Log.e("hanshunli","渲染");
				gl_view.requestRender();
			}
		});
		//SurfaceTexture作为相机预览输出



//		kitkatCamera.setPreviewTexture(mSurfaceTexture);
//
//		kitkatCamera.setDisplayOrientation(90);
//		kitkatCamera.preview();


		return true;
	}

	public void setSurfaceView(GLSurfaceView gl_view) {
		this.gl_view = gl_view;
	}

//	public int getTextureId() {
//		return textureId;
//	}


}
